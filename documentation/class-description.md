**CLASS DEFINITION**

---

# **📌 Class: `ProcessingFragment` (Fragment)**
**Purpose:** Handles the user interface for image processing, including selecting input images, selecting background images, processing images, and saving results.

---

## **🔹 Public Functions**
| Function Name | Visibility | Purpose | Calls Other Functions |
|--------------|------------|---------|----------------------|
| `onCreateView` | `public` | Inflates the fragment layout. | None |
| `onViewCreated` | `public` | Initializes UI elements, sets up event listeners, and observes ViewModel LiveData. | Calls `imagePickerLauncher`, `backgroundPickerLauncher`, `selectBackgroundRegion`, `viewModel.processImage()`, `viewModel.saveImage()` |
| `onBackgroundSelected` | `public` | Updates the UI with the selected cropped background image. | Calls `viewModel.setBackgroundImage()` |

---

## **🔹 Private Functions**
| Function Name | Visibility | Purpose | Calls Other Functions |
|--------------|------------|---------|----------------------|
| `loadBitmap(uri: Uri, isBackground: Boolean)` | `private` | Loads an image from a `Uri` and converts it to a `Bitmap`. Updates `ViewModel` with the image. | Calls `viewModel.setImageUri()`, `viewModel.setBackgroundImage()` |
| `selectBackgroundRegion(uri: Uri)` | `private` | Opens an image selection overlay to allow the user to select a background region. | Calls `SelectionOverlayView.getSelectedRegion()`, `viewModel.setBackgroundImage()` |

---

## **🔹 Lambda Functions & Event Listeners**
| Function/Event | Purpose | Calls Other Functions |
|---------------|---------|----------------------|
| `imagePickerLauncher` | Opens the image picker for selecting the input image. | Calls `loadBitmap()` |
| `backgroundPickerLauncher` | Opens the image picker for selecting a background image. | Calls `loadBitmap()` |
| `SeekBar Change Listeners` | Updates UI when seek bars for threshold, brightness, or tolerance change. | None |
| `btnSelectBgRegion.setOnClickListener` | Selects a background region from the input image. | Calls `selectBackgroundRegion()` |
| `btnProcess.setOnClickListener` | Initiates image processing. | Calls `viewModel.processImage()` |
| `btnSave.setOnClickListener` | Saves the processed image. | Calls `viewModel.saveImage()` |

---

### **📌 Dependencies & Interactions**
- **`ProcessingViewModel`**  
  - Calls `viewModel.setImageUri()`, `viewModel.setBackgroundImage()`, `viewModel.processImage()`, and `viewModel.saveImage()`.
  - Observes `viewModel.edgeBitmap`, `viewModel.transparentBitmap`, `viewModel.isProcessing`, and `viewModel.selectedSize`.

- **`SelectionOverlayView`**  
  - Calls `getSelectedRegion()` to extract the selected background.

---
 