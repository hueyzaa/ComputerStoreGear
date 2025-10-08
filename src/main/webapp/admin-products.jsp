<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Sản phẩm</h1>

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

    <div class="d-flex justify-content-between align-items-center mb-3">
        <a href="${pageContext.request.contextPath}/admin/products/add" class="btn btn-success"><i class="fas fa-plus-circle"></i> Thêm Sản phẩm mới</a>
        <form action="${pageContext.request.contextPath}/admin/products" method="get" class="d-flex align-items-center">
            <label for="sortBy" class="form-label me-2 mb-0">Sắp xếp theo:</label>
            <select name="sortBy" id="sortBy" class="form-select form-select-sm me-2" onchange="this.form.submit()">
                <option value="ProductName" <c:if test="${sortBy == 'ProductName'}">selected</c:if>>Tên Sản phẩm</option>
                <option value="Price" <c:if test="${sortBy == 'Price'}">selected</c:if>>Giá</option>
                <option value="StockQuantity" <c:if test="${sortBy == 'StockQuantity'}">selected</c:if>>Tồn kho</option>
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
                    <th>Tên Sản phẩm</th>
                    <th>SKU</th>
                    <th>Danh mục</th>
                    <th>Thương hiệu</th>
                    <th>Giá</th>
                    <th>Tồn kho</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="product" items="${requestScope.productList}">
                    <tr>
                        <td>${product.productID}</td>
                        <td>${product.productName}</td>
                        <td>${product.sku}</td>
                        <td>${product.category.categoryName}</td>
                        <td>${product.brand.brandName}</td>
                        <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫"/></td>
                        <td>${product.stockQuantity}</td>
                        <td>
                            <c:choose>
                                <c:when test="${product.active}"><span class="badge bg-success">Hoạt động</span></c:when>
                                <c:otherwise><span class="badge bg-danger">Không hoạt động</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/products/edit?id=${product.productID}" class="btn btn-sm btn-warning me-1"><i class="fas fa-edit"></i> Sửa</a>
                            <a href="${pageContext.request.contextPath}/admin/products/delete?id=${product.productID}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này không?');"><i class="fas fa-trash"></i> Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requestScope.productList}">
                    <tr>
                        <td colspan="9" class="text-center text-muted">Không có sản phẩm nào để hiển thị.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <nav aria-label="Page navigation" class="mt-4">
        <ul class="pagination justify-content-center">
            <li class="page-item <c:if test="${currentPage <= 1}">disabled</c:if>">
                <c:url var="prevUrl" value="${pageContext.request.contextPath}/admin/products">
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
                    <c:url var="pageUrl" value="${pageContext.request.contextPath}/admin/products">
                        <c:param name="page" value="${i}"/>
                        <c:param name="size" value="${pageSize}"/>
                        <c:param name="sortBy" value="${sortBy}"/>
                        <c:param name="sortOrder" value="${sortOrder}"/>
                    </c:url>
                    <a class="page-link" href="${pageUrl}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item <c:if test="${currentPage >= totalPages}">disabled</c:if>">
                <c:url var="nextUrl" value="${pageContext.request.contextPath}/admin/products">
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