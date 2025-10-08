<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Đánh giá Sản phẩm</h1>

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
        <form action="${pageContext.request.contextPath}/admin/reviews" method="get" class="d-flex align-items-center">
            <label for="sortBy" class="form-label me-2 mb-0">Sắp xếp theo:</label>
            <select name="sortBy" id="sortBy" class="form-select form-select-sm me-2" onchange="this.form.submit()">
                <option value="CreatedDate" <c:if test="${sortBy == 'CreatedDate'}">selected</c:if>>Ngày tạo</option>
                <option value="Rating" <c:if test="${sortBy == 'Rating'}">selected</c:if>>Điểm đánh giá</option>
                <option value="IsPublished" <c:if test="${sortBy == 'IsPublished'}">selected</c:if>>Trạng thái duyệt</option>
            </select>
            <select name="sortOrder" class="form-select form-select-sm" onchange="this.form.submit()">
                <option value="desc" <c:if test="${sortOrder == 'desc'}">selected</c:if>>Giảm dần</option>
                <option value="asc" <c:if test="${sortOrder == 'asc'}">selected</c:if>>Tăng dần</option>
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
                    <th>Sản phẩm ID</th>
                    <th>Người dùng</th>
                    <th>Điểm</th>
                    <th>Tiêu đề</th>
                    <th>Nội dung</th>
                    <th>Ngày tạo</th>
                    <th>Đã duyệt</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="review" items="${requestScope.reviewList}">
                    <tr>
                        <td>${review.reviewID}</td>
                        <td>${review.productID}</td>
                        <td>${review.userFullName} (<c:out value="${review.username}"/>)</td>
                        <td>${review.rating}</td>
                        <td>${review.title}</td>
                        <td>${review.reviewText}</td>
                        <td><fmt:formatDate value="${review.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${review.published}"><span class="badge bg-success">Có</span></c:when>
                                <c:otherwise><span class="badge bg-warning">Không</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/reviews/toggle-publish?id=${review.reviewID}" class="btn btn-sm btn-info text-white me-1">
                                <c:choose>
                                    <c:when test="${review.published}"><i class="fas fa-eye-slash"></i> Bỏ duyệt</c:when>
                                    <c:otherwise><i class="fas fa-eye"></i> Duyệt</c:otherwise>
                                </c:choose>
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/reviews/delete?id=${review.reviewID}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa đánh giá này không?');"><i class="fas fa-trash"></i> Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requestScope.reviewList}">
                    <tr>
                        <td colspan="9" class="text-center text-muted">Không có đánh giá nào để hiển thị.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <nav aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item <c:if test="${currentPage <= 1}">disabled</c:if>">
                <c:url var="prevUrl" value="${pageContext.request.contextPath}/admin/reviews">
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
                    <c:url var="pageUrl" value="${pageContext.request.contextPath}/admin/reviews">
                        <c:param name="page" value="${i}"/>
                        <c:param name="size" value="${pageSize}"/>
                        <c:param name="sortBy" value="${sortBy}"/>
                        <c:param name="sortOrder" value="${sortOrder}"/>
                    </c:url>
                    <a class="page-link" href="${pageUrl}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item <c:if test="${currentPage >= totalPages}">disabled</c:if>">
                <c:url var="nextUrl" value="${pageContext.request.contextPath}/admin/reviews">
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