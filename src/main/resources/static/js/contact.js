console.log("This is contact page");

// Ensure the DOM is fully loaded before accessing elements
document.addEventListener("DOMContentLoaded", () => {
    const viewContactModal = document.getElementById('view_contact_modal');

    // Options with default values
    const options = {
        placement: 'bottom-right',
        backdrop: 'dynamic',
        backdropClasses:
            'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
        closable: true,
        onHide: () => {
            console.log('modal is hidden');
        },
        onShow: () => {
            console.log('modal is shown');
        },
        onToggle: () => {
            console.log('modal has been toggled');
        },
    };

    // Instance options object
    const instanceOptions = {
        id: 'view_contact_modal',
        override: true
    };

    // Initialize contactModal after DOM content is loaded
    const contactModal = new Modal(viewContactModal, options, instanceOptions);

    // Define the function to open the contact modal
    window.openContactModal = function() {
        contactModal.show();
    };

    window.closeContactModal= function() {
        contactModal.hide();
    };

    window.loadContactData = async function(id) {
        try {
            const response = await fetch(`http://localhost:8081/contacts/${id}`);
            const data = await response.json();
            console.log(data);
            console.log(data.name);
            document.querySelector('#contact_name').value = data.name;
            // Call openContactModal using window object
            window.openContactModal();
        } catch (err) {
            console.log(err);
        }
    };
});
