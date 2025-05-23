#!/bin/bash
echo "Initializing with default db and password"

mongosh <<EOF
use ${MONGO_DB}

db.createUser({
  user: "${MONGO_USER}",
  pwd: "${MONGO_PASSWORD}",
  roles: [
    {
      role: "readWrite",
      db: "${MONGO_DB}"
    }
  ]
});
EOF