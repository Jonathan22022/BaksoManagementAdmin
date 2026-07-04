const coreApi = require("../config/midtrans");
const db = require("../config/firebase");
const response = require("../utils/response");

exports.createQRIS = async (req, res) => {

    try {

        const {
            orderId,
            total
        } = req.body;

        if (!orderId || !total) {

            return response.error(
                res,
                "orderId dan total wajib diisi",
                400
            );
        }

        // Pastikan order ada
        const orderRef = db
            .collection("orders")
            .doc(orderId);

        const orderDoc =
            await orderRef.get();

        if (!orderDoc.exists) {

            return response.error(
                res,
                "Order tidak ditemukan",
                404
            );
        }

        const parameter = {

            payment_type: "qris",

            transaction_details: {

                order_id: orderId,

                gross_amount: total
            }

        };

        const charge =
            await coreApi.charge(parameter);

        const qrAction =
            charge.actions.find(action =>
                action.name === "generate-qr-code"
            );

        if (!qrAction) {

            return response.error(
                res,
                "QRIS gagal dibuat"
            );
        }

        await orderRef.update({

            paymentStatus: "waiting",

            paymentType: "QRIS",

            transactionId:
                charge.transaction_id,

            transactionStatus:
                charge.transaction_status,

            qrUrl:
                qrAction.url,

            expiryTime:
                charge.expiry_time

        });

        return response.success(

            res,

            "QRIS berhasil dibuat",

            {

                transactionId:
                    charge.transaction_id,

                qrUrl:
                    qrAction.url,

                expiryTime:
                    charge.expiry_time

            }

        );

    } catch (err) {

        console.log(err);

        return response.error(
            res,
            err.message
        );
    }

};

exports.notification = async (req, res) => {

    try {

        const notification = req.body;

        console.log(notification);

        const orderId =
            notification.order_id;

        const transactionStatus =
            notification.transaction_status;

        const fraudStatus =
            notification.fraud_status || "";

        let paymentStatus = "waiting";

        let orderStatus = "waiting_payment";

        switch (transactionStatus) {

            case "capture":

                if (fraudStatus === "accept") {

                    paymentStatus = "paid";

                    orderStatus = "pending";
                }

                break;

            case "settlement":

                paymentStatus = "paid";

                orderStatus = "pending";

                break;

            case "pending":

                paymentStatus = "waiting";

                orderStatus = "waiting_payment";

                break;

            case "expire":

                paymentStatus = "expire";

                orderStatus = "expired";

                break;

            case "cancel":

                paymentStatus = "cancel";

                orderStatus = "cancel";

                break;

            case "deny":

                paymentStatus = "failure";

                orderStatus = "payment_failed";

                break;

        }

        await db
            .collection("orders")
            .doc(orderId)
            .update({

                paymentStatus,

                status: orderStatus,

                transactionStatus,

                paidAt:
                    paymentStatus === "paid"
                        ? Date.now()
                        : null

            });

        console.log(

            "Order",

            orderId,

            "->",

            paymentStatus

        );

        return res.status(200).json({

            success: true

        });

    } catch (err) {

        console.log(err);

        return res.status(500).json({

            success: false,

            message: err.message

        });

    }

};