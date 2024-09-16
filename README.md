
---

# Image Collector Tool

**Image Collector Tool**! This Android application is designed for capturing images automatically at specified intervals and embedding metadata into each image. Below you'll find instructions on how to set up and use the app, as well as information about file storage.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [File Storage](#file-storage)
- [Notes](#notes)

## Features

- **Automatic Image Capture**: Configure the app to automatically capture images at a set interval (e.g., every 10 seconds).
- **Metadata Embedding**: Input metadata that will be embedded into each captured image.
- **Flexible Directory Management**: Define a custom directory where images will be stored.

## Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/KIBUTI-SOFTWARE/IMAGE-COLLECTOR-TOOL-ANDROID.git
   cd image-collector-tool
   ```

2. **Open the Project**

   Open the project in Android Studio.

3. **Setup Dependencies**

   Ensure you have the necessary SDKs and dependencies installed. You can do this by syncing the project with Gradle files in Android Studio.

4. **Build and Run**

   Build and run the application on an emulator or physical device from Android Studio.

## Usage

1. **Configure Image Capture Settings**

   - **Open the App**: Launch the app on your device.
   - **Enter Metadata**: In the "Set Metadata" screen, input the directory name and the time interval (in seconds) for automatic image capture.
   - **Save Settings**: Tap the "Save" button to apply your settings.

2. **Start Capturing Images**

   - **Manual Capture**: Tap the "Capture" button to take an image immediately.
   - **Auto Capture**: Toggle the "Start Auto Capture" button to enable/disable automatic image capturing based on the specified interval.

3. **View Captured Images**

   Images are saved in the specified directory. See the [File Storage](#file-storage) section for the directory path.

## File Storage

- **Images Location**: All captured images are stored in the directory you specify in the "Set Metadata" screen.
- **Default Path**: `Android/data/com.example.metadatainputactivity/files/`

   You can access this directory on your PC using the following steps:

   1. **Connect Device to PC**: Use a USB cable to connect your Android device to your PC.
   2. **Enable File Transfer Mode**: On your device, select "File Transfer" mode if prompted.
   3. **Navigate to Directory**: Use your file explorer to navigate to `Android/data/com.example.metadatainputactivity/files/` on your device.

**Note:** It is recommended to use a PC for accessing and managing the folders for easier file handling and transfer.

## Notes

- Ensure that the app has the necessary permissions to access the camera and storage. This includes runtime permissions for devices running Android 6.0 (Marshmallow) and above.
- For automatic image capture, make sure the time interval is set according to your requirements to prevent excessive storage usage.


---
