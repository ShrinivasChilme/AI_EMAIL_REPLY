// Content script for Gmail integration

// Listen for messages from the popup
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === 'getEmailContent') {
        const emailContent = extractEmailContent();
        sendResponse({ emailContent });
    } else if (request.action === 'insertReply') {
        insertReplyIntoComposer(request.reply);
        sendResponse({ success: true });
    }
});

// Extract email content from Gmail
function extractEmailContent() {
    try {
        // Try to find the email body in various Gmail selectors
        const emailBody = document.querySelector('[role="presentation"] .ii');
        
        if (emailBody) {
            return emailBody.innerText;
        }

        // Fallback: look for visible email text
        const emailElements = document.querySelectorAll('[role="article"]');
        if (emailElements.length > 0) {
            return emailElements[0].innerText;
        }

        return '';
    } catch (error) {
        console.error('Error extracting email:', error);
        return '';
    }
}

// Insert reply into Gmail composer
function insertReplyIntoComposer(reply) {
    try {
        // Find the compose area
        const composeArea = document.querySelector('[role="textbox"][aria-label*="Message"]');
        
        if (composeArea) {
            // Set the text
            composeArea.innerHTML = `<div>${reply}</div>`;
            
            // Trigger input event for Gmail to recognize the change
            composeArea.dispatchEvent(new Event('input', { bubbles: true }));
            composeArea.dispatchEvent(new Event('change', { bubbles: true }));
            
            // Focus on the compose area
            composeArea.focus();
            
            console.log('Reply inserted successfully');
        } else {
            console.warn('Compose area not found');
        }
    } catch (error) {
        console.error('Error inserting reply:', error);
    }
}

// Optional: Add a button to Gmail interface for quick access
function addExtensionButton() {
    try {
        // Look for Gmail's toolbar
        const toolbar = document.querySelector('[role="toolbar"]');
        
        if (toolbar && !document.getElementById('ai-reply-btn')) {
            const button = document.createElement('button');
            button.id = 'ai-reply-btn';
            button.textContent = '✨ AI Reply';
            button.style.cssText = `
                background-color: #4CAF50;
                color: white;
                border: none;
                padding: 10px 15px;
                margin: 5px;
                border-radius: 4px;
                cursor: pointer;
                font-weight: 600;
                font-size: 14px;
            `;
            
            button.addEventListener('click', () => {
                chrome.runtime.sendMessage({ action: 'openPopup' });
            });
            
            toolbar.appendChild(button);
        }
    } catch (error) {
        console.log('Could not add extension button to Gmail');
    }
}

// Add button when page loads
window.addEventListener('load', addExtensionButton);

// Re-add button if Gmail dynamically reloads content
setInterval(addExtensionButton, 5000);
