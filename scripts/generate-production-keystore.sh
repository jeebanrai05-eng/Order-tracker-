#!/usr/bin/env bash
set -euo pipefail

# Helper script to create ONE permanent production keystore for Order Tracker release builds.
# NEVER run this more than once for the app's lifetime; save and securely back up the resulting .jks.

KEYSTORE_FILE="${1:-"release-keystore.jks"}"
KEY_ALIAS="${2:-"upload"}"

echo "============================================================"
echo "Order Tracker - Permanent Production Keystore Generator"
echo "============================================================"

if [ -f "$KEYSTORE_FILE" ]; then
  echo "Error: Keystore file '$KEYSTORE_FILE' already exists!"
  echo "Do NOT overwrite an existing keystore! Reusing the same key is mandatory for self-updates."
  exit 1
fi

echo "Creating permanent production keystore: $KEYSTORE_FILE"
echo "Key alias: $KEY_ALIAS"
echo ""
echo "Please enter a strong password for your keystore and key."
echo "Store this password in a secure password manager!"
echo ""

read -s -p "Enter Keystore & Key Password: " STORE_PASS
echo ""
read -s -p "Confirm Password: " STORE_PASS_CONFIRM
echo ""

if [ "$STORE_PASS" != "$STORE_PASS_CONFIRM" ]; then
  echo "Error: Passwords do not match!"
  exit 1
fi

if [ ${#STORE_PASS} -lt 8 ]; then
  echo "Error: Password must be at least 8 characters long."
  exit 1
fi

keytool -genkeypair -v \
  -keystore "$KEYSTORE_FILE" \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "$STORE_PASS" \
  -keypass "$STORE_PASS" \
  -dname "CN=OrderTrackerRelease, O=OrderTracker, C=US"

echo ""
echo "============================================================"
echo "Production Keystore successfully created!"
echo "============================================================"
echo ""
echo "Certificate Fingerprint (SHA-256):"
keytool -list -v -keystore "$KEYSTORE_FILE" -alias "$KEY_ALIAS" -storepass "$STORE_PASS" | grep -i "SHA256:"
echo ""
echo "------------------------------------------------------------"
echo "GitHub Secrets Configuration Instructions:"
echo "------------------------------------------------------------"
echo "1. Go to your GitHub repository -> Settings -> Secrets and variables -> Actions"
echo "2. Add the following repository secrets:"
echo ""
echo "   RELEASE_KEY_ALIAS:     $KEY_ALIAS"
echo "   RELEASE_STORE_PASSWORD: [Your Password]"
echo "   RELEASE_KEY_PASSWORD:   [Your Password]"
echo ""
echo "   RELEASE_KEYSTORE_BASE64:"
echo "   (Copy the Base64 output below)"
echo "------------------------------------------------------------"
base64 "$KEYSTORE_FILE" | tr -d '\n'
echo ""
echo "------------------------------------------------------------"
echo "CRITICAL SECURITY WARNING:"
echo "* BACK UP '$KEYSTORE_FILE' and its password to a secure location (e.g. 1Password/Bitwarden) NOW."
echo "* NEVER commit '$KEYSTORE_FILE' to git."
echo "* If you lose this keystore, existing installed apps CANNOT be updated seamlessly."
echo "============================================================"
