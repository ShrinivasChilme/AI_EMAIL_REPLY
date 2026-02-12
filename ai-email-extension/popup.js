// Configuration
const API_URL = 'http://localhost:8082/api/email/generate';

// DOM Elements
const emailContentEl = document.getElementById('emailContent');
const toneEl = document.getElementById('tone');
const generateBtn = document.getElementById('generateBtn');
const loadingEl = document.getElementById('loading');
const resultEl = document.getElementById('result');
const resultTextEl = document.getElementById('resultText');
const errorEl = document.getElementById('error');
const errorTextEl = document.getElementById('errorText');
const copyBtn = document.getElementById('copyBtn');
const insertBtn = document.getElementById('insertBtn');
const statusIndicator = document.getElementById('statusIndicator');

let generatedReply = '';

// Generate Reply
generateBtn.addEventListener('click', async () => {
    const emailContent = emailContentEl.value.trim();
    const tone = toneEl.value;

    if (!emailContent) {
        showError('Please enter email content');
        return;
    }

    showLoading(true);
    hideError();
    hideResult();

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                emailContent,
                tone
            })
        });

        const data = await response.text();

        if (!response.ok) {
            throw new Error(data || 'Failed to generate reply');
        }

        if (data.startsWith('Error') || data.startsWith('Unexpected')) {
            throw new Error(data);
        }

        generatedReply = data;
        showResult(data);
    } catch (error) {
        console.error('Error:', error);
        showError(error.message || 'Failed to generate reply. Make sure the backend is running.');
    } finally {
        showLoading(false);
    }
});

// Copy to Clipboard
copyBtn.addEventListener('click', () => {
    navigator.clipboard.writeText(generatedReply).then(() => {
        copyBtn.textContent = 'Copied!';
        setTimeout(() => {
            copyBtn.textContent = 'Copy to Clipboard';
        }, 2000);
    }).catch(err => {
        console.error('Failed to copy:', err);
        showError('Failed to copy to clipboard');
    });
});

// Insert in Gmail
insertBtn.addEventListener('click', () => {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        chrome.tabs.sendMessage(tabs[0].id, {
            action: 'insertReply',
            reply: generatedReply
        });
        insertBtn.textContent = 'Inserted!';
        setTimeout(() => {
            insertBtn.textContent = 'Insert in Gmail';
        }, 2000);
    });
});

// UI Helper Functions
function showLoading(show) {
    loadingEl.style.display = show ? 'block' : 'none';
}

function showResult(text) {
    resultTextEl.textContent = text;
    resultEl.style.display = 'block';
}

function hideResult() {
    resultEl.style.display = 'none';
}

function showError(message) {
    errorTextEl.textContent = message;
    errorEl.style.display = 'block';
}

function hideError() {
    errorEl.style.display = 'none';
}

// Check Backend Status
async function checkBackendStatus() {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ emailContent: 'test', tone: 'professional' })
        });
        // We expect an error response (missing valid email), but the important thing is the endpoint responds
        if (response.status >= 200 && response.status < 500) {
            statusIndicator.textContent = 'Connected ✓';
            statusIndicator.className = 'status-connected';
        } else {
            statusIndicator.textContent = 'Disconnected ✗';
            statusIndicator.className = 'status-disconnected';
        }
    } catch (error) {
        statusIndicator.textContent = 'Disconnected ✗';
        statusIndicator.className = 'status-disconnected';
    }
}

// Check backend status on load
document.addEventListener('DOMContentLoaded', checkBackendStatus);

// Pre-fill email content from Gmail if available
chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    chrome.tabs.sendMessage(tabs[0].id, { action: 'getEmailContent' }, (response) => {
        if (response && response.emailContent) {
            emailContentEl.value = response.emailContent;
        }
    });
});
