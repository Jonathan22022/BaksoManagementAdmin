exports.success = (
    res,
    message,
    data = {}
) => {

    return res.status(200).json({

        success: true,

        message,

        data
    });

};

exports.error = (
    res,
    message,
    status = 500
) => {

    return res.status(status).json({

        success: false,

        message
    });

};