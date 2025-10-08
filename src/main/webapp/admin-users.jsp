<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Người dùng</h1>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success text-center" role="alert">
            ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="d-flex justify-content-end align-items-center mb-3">
        <form action="${pageContext.request.contextPath}/admin/users" method="get" class="d-flex align-items-center">
            <label for="sortBy" class="form-label me-2 mb-0">Sắp xếp theo:</label>
            <select name="sortBy" id="sortBy" class="form-select form-select-sm me-2" onchange="this.form.submit()">
                <option value="Username" <c:if test="${sortBy == 'Username'}">selected</c:if>>Tên đăng nhập</option>
                <option value="Email" <c:if test="${sortBy == 'Email'}">selected</c:if>>Email</option>
                <option value="CreatedDate" <c:if test="${sortBy == 'CreatedDate'}">selected</c:if>>Ngày tạo</option>
            </select>
            <select name="sortOrder" class="form-select form-select-sm" onchange="this.form.submit()">
                <option value="asc" <c:if test="${sortOrder == 'asc'}">selected</c:if>>Tăng dần</option>
                <option value="desc" <c:if test="${sortOrder == 'desc'}">selected</c:if>>Giảm dần</option>
            </select>
            <input type="hidden" name="page" value="${currentPage}">
            <input type="hidden" name="size" value="${pageSize}">
        </form>
    </div>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Tên</th>
                    <th>Họ</th>
                    <th>Vai trò</th>
                    <th>Trạng thái</th>
                    <th>Email đã xác minh</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${requestScope.userList}">
                    <tr>
                        <td>${user.userID}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.role}</td>
                        <td>
                            <c:choose>
                                <c:when test="${user.active}"><span class="badge bg-success">Hoạt động</span></c:when>
                                <c:otherwise><span class="badge bg-danger">Không hoạt động</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${user.emailVerified}"><span class="badge bg-success">Có</span></c:when>
                                <c:otherwise><span class="badge bg-danger">Không</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.userID}" class="btn btn-sm btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requestScope.userList}">
                    <tr>
                        <td colspan="9" class="text-center text-muted">Không có người dùng nào để hiển thị.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <nav aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item <c:if test="${currentPage <= 1}">disabled</c:if>">
                <c:url var="prevUrl" value="${pageContext.request.contextPath}/admin/users">
                    <c:param name="page" value="${currentPage - 1}"/>
                    <c:param name="size" value="${pageSize}"/>
                    <c:param name="sortBy" value="${sortBy}"/>
                    <c:param name="sortOrder" value="${sortOrder}"/>
                </c:url>
                <a class="page-link" href="${prevUrl}" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item <c:if test="${i == currentPage}">active</c:if>">
                    <c:url var="pageUrl" value="${pageContext.request.contextPath}/admin/users">
                        <c:param name="page" value="${i}"/>
                        <c:param name="size" value="${pageSize}"/>
                        <c:param name="sortBy" value="${sortBy}"/>
                        <c:param name="sortOrder" value="${sortOrder}"/>
                    </c:url>
                    <a class="page-link" href="${pageUrl}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item <c:if test="${currentPage >= totalPages}">disabled</c:if>">
                <c:url var="nextUrl" value="${pageContext.request.contextPath}/admin/users">
                    <c:param name="page" value="${currentPage + 1}"/>
                    <c:param name="size" value="${pageSize}"/>
                    <c:param name="sortBy" value="${sortBy}"/>
                    <c:param name="sortOrder" value="${sortOrder}"/>
                </c:url>
                <a class="page-link" href="${nextUrl}" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>
        </ul>
    </nav>
</div>

<jsp:include page="admin-footer.jsp" />