document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
    // Gọi API hoặc submit form xoá ở đây

    // Đóng modal sau khi xoá
    var modal = bootstrap.Modal.getInstance(document.getElementById('confirmDeleteModal'));
    modal.hide();
});
