const express = require("express");

const router = express.Router();

const paymentController =
require("../controllers/paymentControllers");

const verifySignature =
require("../middleware/verifySignature");

router.post(
    "/create-qris",
    paymentController.createQRIS
);

router.post(
    "/notification",
    verifySignature,
    paymentController.notification
);

module.exports = router;