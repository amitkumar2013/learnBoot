-- CREATE test-user:test-password@localhost:27017/testdb IF NOT EXISTS followed by table
use admin 
db.createUser
(
   { user: "test-user",
     pwd:  "test-password",
     roles: [ { role: "readWrite",  db: "testdb" }],
   }
);