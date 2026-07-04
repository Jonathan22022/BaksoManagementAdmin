const db = require("../config/firebase");

const response =
require("../utils/response");

exports.getOrder = async (

    req,

    res

) => {

    try{

        const doc = await db

            .collection("orders")

            .doc(req.params.id)

            .get();

        if(!doc.exists){

            return response.error(

                res,

                "Order tidak ditemukan",

                404

            );

        }

        response.success(

            res,

            "Success",

            doc.data()

        );

    }

    catch(e){

        response.error(

            res,

            e.message

        );

    }

};

exports.getOrders = async (

    req,

    res

)=>{

    try{

        const snapshot =

        await db

        .collection("orders")

        .get();

        const orders=[];

        snapshot.forEach(doc=>{

            orders.push({

                id:doc.id,

                ...doc.data()

            });

        });

        response.success(

            res,

            "Success",

            orders

        );

    }

    catch(e){

        response.error(

            res,

            e.message

        );

    }

};