#!/bin/bash
set -e

if [ -z "$OPENSEARCH_INITIAL_ADMIN_PASSWORD" ]; then
  echo "Error: environment variable OPENSEARCH_INITIAL_ADMIN_PASSWORD is not set. Exiting."
  exit 1
fi

if [ -z "$OPENSEARCH_USERNAME" ] || [ -z "$OPENSEARCH_PASSWORD" ]; then
  echo "Error: environment variables OPENSEARCH_USERNAME and OPENSEARCH_PASSWORD must be set. Exiting."
  exit 1
fi

# base64-encoded "admin:password"
BASIC_AUTH=$(echo -n "admin:$OPENSEARCH_INITIAL_ADMIN_PASSWORD" | base64)
echo "Authorization header (base64): $BASIC_AUTH"

echo "Creating or updating user: $OPENSEARCH_USERNAME"
HTTP_CODE=$(curl -k -s -o /dev/null -w "%{http_code}" \
  -XPUT "https://geo-opensearch:9200/_plugins/_security/api/internalusers/$OPENSEARCH_USERNAME" \
  -H "Authorization: Basic $BASIC_AUTH" \
  -H "Content-Type: application/json" \
  -d "{
    \"password\": \"$OPENSEARCH_PASSWORD\",
    \"backend_roles\": [\"admin\"]
  }"
)

if [ "$HTTP_CODE" -ne 200 ] && [ "$HTTP_CODE" -ne 201 ]; then
  echo "Error: failed to create or update user. HTTP status: $HTTP_CODE"
  exit 1
fi

echo "User $OPENSEARCH_USERNAME created or updated successfully."