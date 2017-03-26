const MESSAGE_TYPE_IDENTIFY  = 1
const MESSAGE_TYPE_SEND_CHAT = 2


const Parse = require('parse/node');
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

var mChatrooms = [];
var mClients = [];

Parse.initialize("I6DUDtMs1nxNl3w4MA794RSqBGmgPl7gnlJrjrtW", "OZhKnHGpi8Y5d1S2aogox7osgNGS8Hgr8xwBEp9p");
Parse.serverURL = 'https://parseapi.back4app.com'

//Get chatrooms from database
var Chatrooms = Parse.Object.extend("Chatrooms");
var query = new Parse.Query(Chatrooms);

query.find({
    success: function(results) {
        //Keep track of all chatrooms
        for (var i = 0; i < results.length; i++) {
            var object = results[i];

            console.log("Chatroom " + i + " members");
            
            object.relation("members").query().find({
                success: function(members){
                    console.log("Got Members");
                    mChatrooms[object.id] = {"clients":[]};
                    
                    var newRoom = mChatrooms[object.id];
                    
                    console.log(members.length);
                    for (x = 0; x < members.length; ++x) {
                        newRoom.clients.push(members[x].id);
                    }
                    
                     console.log("Finished Members");
                },
                error: function(error){
                    console.log(error);
                }
            });
            
        }
    },
    error: function(error) {
        console.log("Error: " + error.code + " " + error.message);
    }
});


wss.on('connection', function connection(ws) {
    ws.on('message', function(message) {
        msgJson = JSON.parse(message);

        console.log(message);
        console.log("Type " + msgJson.mType);
        switch (msgJson.mType) {
            case MESSAGE_TYPE_IDENTIFY:
                mClients[msgJson.clientId] = ws;
                
                console.log(mClients);
                break;
            case MESSAGE_TYPE_SEND_CHAT:
                sendChatMsg(msgJson, ws)
                break;

        }
    });
});

var sendChatMsg = function(msgJson, ws) {
    console.log("prepping to send")
    console.log(mChatrooms);
    var room = mChatrooms[msgJson.chatroomId];
            
    //Go through every connected client
    room.clients.forEach(function each(member) {
        //Send data to that client
        var client = mClients[member];
        if (client !== ws && client.readyState === WebSocket.OPEN) {
            console.log("Sending Message");
            client.send(msgJson.msg);
        }
    });       
}