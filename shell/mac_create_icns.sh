#!/bin/bash

# Ensure the script is running on macOS
if [[ "$(uname)" != "Darwin" ]]; then
    echo "This script is intended to be run on macOS."
    exit 1
fi

# Prompt user for the source PNG file
read -p "Enter the source PNG file name (e.g. my_app_icon.png): " SOURCE_PNG

# Check if the file exists
if [ ! -f "$SOURCE_PNG" ]; then
    echo "Error: File '$SOURCE_PNG' not found."
    exit 1
fi

# Prompt for output name (without extension), default to "icon" if empty
read -p "Enter the output icon name (default: icon): " OUTPUT_NAME
OUTPUT_NAME=${OUTPUT_NAME:-icon}

# Create a temporary iconset directory
ICONSET_DIR="${OUTPUT_NAME}.iconset"
rm -rf "$ICONSET_DIR"
mkdir "$ICONSET_DIR"

echo "Generating iconset from '$SOURCE_PNG'..."

# Generate icons of various sizes
sips -z 16 16     "$SOURCE_PNG" --out "$ICONSET_DIR/icon_16x16.png"
sips -z 32 32     "$SOURCE_PNG" --out "$ICONSET_DIR/icon_16x16@2x.png"
sips -z 32 32     "$SOURCE_PNG" --out "$ICONSET_DIR/icon_32x32.png"
sips -z 64 64     "$SOURCE_PNG" --out "$ICONSET_DIR/icon_32x32@2x.png"
sips -z 128 128   "$SOURCE_PNG" --out "$ICONSET_DIR/icon_128x128.png"
sips -z 256 256   "$SOURCE_PNG" --out "$ICONSET_DIR/icon_128x128@2x.png"
sips -z 256 256   "$SOURCE_PNG" --out "$ICONSET_DIR/icon_256x256.png"
sips -z 512 512   "$SOURCE_PNG" --out "$ICONSET_DIR/icon_256x256@2x.png"
sips -z 512 512   "$SOURCE_PNG" --out "$ICONSET_DIR/icon_512x512.png"
sips -z 1024 1024 "$SOURCE_PNG" --out "$ICONSET_DIR/icon_512x512@2x.png"

# Convert the iconset to .icns format
echo "Creating ${OUTPUT_NAME}.icns..."
iconutil -c icns "$ICONSET_DIR" -o "${OUTPUT_NAME}.icns"

# Check result
if [ -f "${OUTPUT_NAME}.icns" ]; then
    echo "✅ Successfully created '${OUTPUT_NAME}.icns'."
else
    echo "❌ Error: Failed to create '${OUTPUT_NAME}.icns'."
fi

# Cleanup
rm -rf "$ICONSET_DIR"