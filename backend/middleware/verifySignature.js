const crypto = require("crypto");

module.exports = (req, res, next) => {

    const {

        order_id,

        status_code,

        gross_amount,

        signature_key

    } = req.body;

    const hash = crypto
        .createHash("sha512")
        .update(
            order_id +
            status_code +
            gross_amount +
            process.env.MIDTRANS_SERVER_KEY
        )
        .digest("hex");

    if (hash !== signature_key) {

        return res.status(401).json({

            message: "Invalid Signature"

        });

    }

    next();

};