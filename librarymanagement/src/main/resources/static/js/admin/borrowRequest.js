function acceptRequest(id) {
    fetch(`/admin/borrow-requests/accept/${id}`, {
        method: "PATCH"
    }).then(res => {
        if (res.ok) {
            location.reload();
        }
    });
}