# **App User Manual**

## **Table of Content**
1. [App Menu](#app-menu)  
2. [Home](#home)  
3. [Processing](#processing)  
4. [Settings](#settings)  
5. [Gallery (To be done)](#gallery)  
6. [About](#about)  

---

## **App Menu**  
The app menu consists of the following sections:  
- **Home** – Introduction to the app.  
- **Processing** – Image processing tools and options.  
- **Settings** – Modify default parameters.  
- **Gallery** *(To be done)* – View processed images, export options.  
- **About** – App version and developer details.  

---

## **Home**  
The **Home** section presents the app and its functionalities. It serves as the landing page where users can access all features.  

---

## **Processing**  
The **Processing** screen provides tools for image background removal and edge detection.  

### **Features:**
1. **Select Image** → Choose an image from your device.  
2. **Select Background** → Choose a background reference image (optional).  
3. **Select Background from Image** (New feature) → Opens the selected image in full-screen to allow manual selection of a background region using click & drag.  
4. **Adjust Parameters:**
   - **Tolerance:** Adjusts the sensitivity for background removal.  
   - **Brightness:** Controls the image brightness.  
   - **Threshold 1 & 2:** Modify edge detection levels.  
   - **Live Updates:** As you adjust tolerance, brightness, or thresholds in the spinner, the new value is displayed on the right.  
5. **Process Image** → Applies background removal and edge detection.  
6. **View Results:** Processed images appear in the preview section.  
7. **Save Image** → Saves the processed image to the selected directory.  

---

## **Select Background from Image**  
1. **User selects a background image** → Opens in full-screen.  
2. **User clicks & drags** → A red rectangle appears to mark the selected area.  
3. **Press "OK"** → The selected **region is extracted** as a `Bitmap`.  
4. **App uses this as the background reference** for color detection.  

*If no background image is selected, the top-left 10% of the image will be used as the default background reference.*  

---

## **Settings**  
Users can modify default settings:  
- **Default Image Size** – Choose from predefined sizes (S, M, L, XL, XXL).  
- **Save Location** – Select where processed images are stored.  
- **Cloud Export Settings (Upcoming Feature)** – Configure cloud export preferences.  

---

## **Gallery (To be done)**  
The **Gallery** section will allow users to:  
- View processed images in a **thumbnail grid view**.  
- See **image details**:  
  - Name  
  - Date of creation  
  - File size  
- **Click on an image** to access export options.  
- **Export to Cloud (Upcoming Feature)** – Users will be able to upload images to a configured cloud storage (settings managed in the **Settings** section).  

---

## **About**  
- Displays the **app version** and **developer details**.  
- Provides **support/contact information**.  

--- 