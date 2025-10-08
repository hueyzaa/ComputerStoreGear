<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Sửa Người dùng: ${requestScope.userToEdit.username}</h1>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/admin/users/edit" method="post">
            <input type="hidden" name="userId" value="${requestScope.userToEdit.userID}">

            <div class="mb-3">
                <label for="username" class="form-label">Tên đăng nhập:</label>
                <input type="text" class="form-control" id="username" name="username" value="${requestScope.userToEdit.username}" readonly>
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">Email:</label>
                <input type="email" class="form-control" id="email" name="email" value="${requestScope.userToEdit.email}" readonly>
            </div>
            <div class="mb-3">
                <label for="role" class="form-label">Vai trò:</label>
                <select class="form-select" id="role" name="role" required>
                    <option value="Customer" <c:if test="${requestScope.userToEdit.role == 'Customer'}">selected</c:if>>Customer</option>
                    <option value="Admin" <c:if test="${requestScope.userToEdit.role == 'Admin'}">selected</c:if>>Admin</option>
                </select>
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" <c:if test="${requestScope.userToEdit.active}">checked</c:if>>
                <label class="form-check-label" for="isActive">Hoạt động</label>
            </div>

            <button type="submit" class="btn btn-primary w-100">Cập nhật Người dùng</button>
        </form>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Quay lại danh sách người dùng</a>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />