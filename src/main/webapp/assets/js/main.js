/**
 * Online Shopping System - Main JavaScript Utility
 * Handles AJAX Add-to-Cart, Toast Alerts, Modal Dialogs, and Client Validation.
 */

// Toast notification helper
function showToast(message, type = 'success') {
    let toast = document.getElementById('app-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'app-toast';
        toast.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            padding: 14px 22px;
            border-radius: 8px;
            color: white;
            font-weight: 600;
            font-size: 0.95rem;
            z-index: 9999;
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.2);
            transition: opacity 0.3s ease, transform 0.3s ease;
            opacity: 0;
            transform: translateY(20px);
        `;
        document.body.appendChild(toast);
    }

    toast.style.backgroundColor = (type === 'success') ? '#16a34a' : '#dc2626';
    toast.textContent = message;
    toast.style.opacity = '1';
    toast.style.transform = 'translateY(0)';

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
    }, 3500);
}

// Asynchronous AJAX Add to Cart
function ajaxAddToCart(productId, quantity = 1, contextPath = '') {
    const formData = new URLSearchParams();
    formData.append('productId', productId);
    formData.append('quantity', quantity);

    fetch(contextPath + '/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: formData.toString()
    })
    .then(response => {
        if (response.redirected) {
            window.location.href = response.url;
            return;
        }
        return response.json();
    })
    .then(data => {
        if (data) {
            if (data.success) {
                showToast(data.message, 'success');
                // Update badge in navbar
                const badge = document.querySelector('.cart-badge');
                if (badge) {
                    badge.textContent = data.cartCount;
                    badge.style.display = data.cartCount > 0 ? 'inline-block' : 'none';
                }
            } else {
                showToast(data.message, 'error');
            }
        }
    })
    .catch(error => {
        console.error('Error adding to cart:', error);
        showToast('Could not add item to cart. Please try again.', 'error');
    });
}

// Modal popup triggers
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Client-side image validation (type & max 2MB)
function validateImageInput(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        const validExtensions = ['image/jpeg', 'image/png', 'image/webp'];
        const maxSize = 2 * 1024 * 1024; // 2 MB

        if (!validExtensions.includes(file.type)) {
            alert('Invalid file format. Please upload JPG, PNG, or WEBP only.');
            input.value = '';
            return false;
        }

        if (file.size > maxSize) {
            alert('File exceeds maximum size of 2 MB.');
            input.value = '';
            return false;
        }
    }
    return true;
}
