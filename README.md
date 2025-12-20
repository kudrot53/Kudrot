
 # Contact Form with Google Sheets Integration

A beautiful, responsive contact form that automatically saves submissions to Google Sheets. No backend server required!

![Form Preview](https://img.shields.io/badge/HTML-Form-orange) ![Google Sheets](https://img.shields.io/badge/Google-Sheets-green) ![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow)

## Features

✨ **Modern Design** - Clean, gradient-based UI with smooth animations  
📱 **Fully Responsive** - Works perfectly on mobile, tablet, and desktop  
☁️ **Cloud Storage** - All submissions saved directly to Google Sheets  
✅ **Form Validation** - Built-in HTML5 validation with custom styling  
🚀 **No Backend Required** - Uses Google Apps Script as serverless backend  
⚡ **Real-time Feedback** - Success/error messages for user actions  

## Demo

The form collects the following information:
- Full Name (required)
- Email Address (required)
- Phone Number (optional)
- Subject (required dropdown)
- Message (required)
- Timestamp (auto-generated)

## Installation & Setup

### Step 1: Download the HTML File

Save the `contact-form.html` file to your computer.

### Step 2: Create a Google Sheet

1. Go to [Google Sheets](https://sheets.google.com)
2. Create a new blank spreadsheet
3. Name it (e.g., "Form Responses")

### Step 3: Set Up Google Apps Script

1. In your Google Sheet, click **Extensions** → **Apps Script**
2. Delete any existing code
3. Copy and paste the following code:

```javascript
function doPost(e) {
  try {
    var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    var data = JSON.parse(e.postData.contents);
    
    if (sheet.getLastRow() === 0) {
      sheet.appendRow(['Timestamp', 'Name', 'Email', 'Phone', 'Subject', 'Message']);
    }
    
    sheet.appendRow([
      data.timestamp || new Date().toLocaleString(),
      data.name || '',
      data.email || '',
      data.phone || '',
      data.subject || '',
      data.message || ''
    ]);
    
    return ContentService
      .createTextOutput(JSON.stringify({
        status: 'success',
        message: 'Data saved successfully'
      }))
      .setMimeType(ContentService.MimeType.JSON);
    
  } catch (error) {
    return ContentService
      .createTextOutput(JSON.stringify({
        status: 'error',
        message: error.toString()
      }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}
```

4. Click **Save** (💾 icon)
5. Name your project (e.g., "Form Handler")

### Step 4: Deploy as Web App

1. Click **Deploy** → **New deployment**
2. Click the gear icon (⚙️) and select **Web app**
3. Configure settings:
   - **Description**: Form submission handler (optional)
   - **Execute as**: Me
   - **Who has access**: Anyone
4. Click **Deploy**
5. **Authorize** the app when prompted:
   - Click **Authorize access**
   - Select your Google account
   - Click **Advanced** → **Go to [project name] (unsafe)**
   - Click **Allow**
6. **Copy the Web App URL** (looks like: `https://script.google.com/macros/s/ABC123.../exec`)

### Step 5: Update HTML File

1. Open `contact-form.html` in a text editor
2. Find this line (around line 150):
   ```javascript
   const SCRIPT_URL = 'https://script.google.com/macros/s/YOUR_URL_HERE/exec';
   ```
3. Replace `YOUR_URL_HERE` with your actual Web App URL
4. Save the file

### Step 6: Test Your Form

1. Open `contact-form.html` in a web browser
2. Fill out all required fields
3. Click **Submit Form**
4. Check your Google Sheet - you should see a new row with the submitted data!

## File Structure

```
contact-form-project/
│
├── contact-form.html          # Main HTML form file
├── README.md                  # This file
└── Code.gs                    # Google Apps Script (in Google Apps Script editor)
```

## Technologies Used

- **HTML5** - Structure and form elements
- **CSS3** - Styling with gradients and animations
- **JavaScript (ES6)** - Form handling and API communication
- **Google Apps Script** - Serverless backend
- **Google Sheets API** - Data storage

## Customization

### Change Form Colors

Edit the CSS gradient in the `<style>` section:

```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```

Replace with your preferred colors.

### Add More Fields

1. Add HTML input in the form:
```html
<div class="form-group">
    <label for="company">Company</label>
    <input type="text" id="company" name="company">
</div>
```

2. Update Google Apps Script to include the new field:
```javascript
sheet.appendRow([
    data.timestamp,
    data.name,
    data.email,
    data.phone,
    data.subject,
    data.message,
    data.company  // Add new field
]);
```

### Modify Subject Options

Edit the `<select>` dropdown options in the HTML:

```html
<option value="Your Option">Your Option</option>
```

## Troubleshooting

### Form shows error message

**Issue**: "Error submitting form"

**Solutions**:
- Verify your Google Apps Script URL is correct
- Make sure deployment settings have "Who has access" set to **Anyone**
- Check browser console (F12) for detailed errors

### Data not appearing in Google Sheet

**Issue**: Form submits successfully but no data in sheet

**Solutions**:
- Check if the script is authorized properly
- Run the script test function in Apps Script editor
- Verify the sheet is the active sheet in your spreadsheet

### CORS or Network Errors

**Issue**: CORS policy blocking requests

**Solutions**:
- The form uses `mode: 'no-cors'` by default
- Ensure your Google Apps Script is deployed with public access
- Try redeploying the script with a new deployment

## Redeploying After Changes

If you make changes to your Google Apps Script:

1. Click **Deploy** → **Manage deployments**
2. Click the **Edit** icon (pencil) next to your deployment
3. Click **Deploy**
4. Update the URL in your HTML file if it changed

## Security Notes

- Form data is sent over HTTPS
- Google Apps Script runs with your credentials
- Only you can access the spreadsheet (unless you share it)
- No sensitive data should be collected without proper encryption
- The script has public access but can only write to your specific sheet

## Browser Compatibility

- ✅ Chrome (recommended)
- ✅ Firefox
- ✅ Safari
- ✅ Edge
- ✅ Mobile browsers

## License

This project is free to use for personal and commercial purposes.

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review Google Apps Script documentation
3. Verify all setup steps were completed correctly

## Credits

Created using HTML, CSS, JavaScript, and Google Apps Script.

---

**Made with ❤️ for easy form-to-sheet integration**
