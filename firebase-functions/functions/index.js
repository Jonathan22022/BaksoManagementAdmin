/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */


// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyStatusChanged = functions.firestore
    .document("orders/{orderId}")
    .onUpdate(async (change, context) => {
      const before = change.before.data();
      const after = change.after.data();

      console.log("========== ORDER UPDATE ==========");
      console.log("orderId :", context.params.orderId);
      console.log("before  :", before.status);
      console.log("after   :", after.status);
      console.log("userId  :", after.userID);

      if (before.status === after.status) {
        console.log("STATUS TIDAK BERUBAH");
        return null;
      }

      const userId = after.userID;

      const userDoc = await admin.firestore()
          .collection("users")
          .doc(userId)
          .get();

      if (!userDoc.exists) {
        console.log("USER TIDAK DITEMUKAN");
        return null;
      }

      const token = userDoc.data().fcmToken;

      console.log("TOKEN :", token);

      if (!token) {
        console.log("FCM TOKEN KOSONG");
        return null;
      }

      const title = "Status Pesanan";
      let body = "";

      switch (after.status) {
        case "diproses":
          body = "Pesanan Anda sedang diproses";
          break;

        case "siap_diambil":
          body = "Pesanan Anda siap diambil";
          break;

        case "cancel":
          body = "Pesanan Anda dibatalkan";
          break;

        default:
          console.log("STATUS TIDAK MEMBUTUHKAN NOTIFIKASI");
          return null;
      }

      console.log("KIRIM NOTIFIKASI");
      console.log("TITLE :", title);
      console.log("BODY  :", body);

      try {
        const response = await admin.messaging().send({
          token: token,
          notification: {
            title: title,
            body: body,
          },
          data: {
            orderId: context.params.orderId,
            userId: userId,
            status: after.status,
          },
        });

        console.log("NOTIFIKASI BERHASIL", response);

        return response;
      } catch (error) {
        console.error(
            "GAGAL KIRIM NOTIFIKASI",
            error,
        );

        return null;
      }
    });
