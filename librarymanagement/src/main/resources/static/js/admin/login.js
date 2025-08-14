document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    form?.addEventListener('submit', (event) => {
        event.preventDefault();
        event.stopPropagation();

        if (form.checkValidity()) {
            console.log('Form is valid, submitting...');
            form.submit();
        }

        form.classList.add('was-validated');
    });

    const inputs = form?.querySelectorAll('input');
    inputs?.forEach(input => {
        input.addEventListener('input', () => {
            if (input.value) {
                input.classList.remove('is-invalid');
            }
        });
    });
});