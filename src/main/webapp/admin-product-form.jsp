<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4"><c:choose><c:when test="${not empty requestScope.product}">Sửa Sản phẩm</c:when><c:otherwise>Thêm Sản phẩm mới</c:otherwise></c:choose></h1>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/admin/products/<c:choose><c:when test="${not empty requestScope.product}">edit</c:when><c:otherwise>add</c:otherwise></c:choose>" method="post">
            <c:if test="${not empty requestScope.product}">
                <input type="hidden" name="productId" value="${requestScope.product.productID}">
            </c:if>

            <div class="mb-3">
                <label for="productName" class="form-label">Tên Sản phẩm:</label>
                <input type="text" class="form-control" id="productName" name="productName" value="${requestScope.product.productName}" required>
            </div>
            <div class="mb-3">
                <label for="sku" class="form-label">SKU:</label>
                <input type="text" class="form-control" id="sku" name="sku" value="${requestScope.product.sku}">
            </div>
            <div class="mb-3">
                <label for="categoryId" class="form-label">Danh mục:</label>
                <select class="form-select" id="categoryId" name="categoryId" required>
                    <option value="">-- Chọn Danh mục --</option>
                    <c:forEach var="category" items="${requestScope.categories}">
                        <option value="${category.categoryID}" <c:if test="${category.categoryID == requestScope.product.categoryID}">selected</c:if>>${category.categoryName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label for="brandId" class="form-label">Thương hiệu:</label>
                <select class="form-select" id="brandId" name="brandId">
                    <option value="">-- Chọn Thương hiệu --</option>
                    <c:forEach var="brand" items="${requestScope.brands}">
                        <option value="${brand.brandID}" <c:if test="${brand.brandID == requestScope.product.brandID}">selected</c:if>>${brand.brandName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô tả:</label>
                <textarea class="form-control" id="description" name="description" rows="4">${requestScope.product.description}</textarea>
            </div>
            <div class="mb-3">
                <label for="shortDescription" class="form-label">Mô tả ngắn:</label>
                <input type="text" class="form-control" id="shortDescription" name="shortDescription" value="${requestScope.product.shortDescription}">
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="price" class="form-label">Giá:</label>
                    <input type="number" class="form-control" id="price" name="price" step="0.01" value="${requestScope.product.price}" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="comparePrice" class="form-label">Giá so sánh:</label>
                    <input type="number" class="form-control" id="comparePrice" name="comparePrice" step="0.01" value="${requestScope.product.comparePrice}">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="costPrice" class="form-label">Giá vốn:</label>
                    <input type="number" class="form-control" id="costPrice" name="costPrice" step="0.01" value="${requestScope.product.costPrice}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="weight" class="form-label">Cân nặng (kg):</label>
                    <input type="number" class="form-control" id="weight" name="weight" step="0.01" value="${requestScope.product.weight}">
                </div>
            </div>
            <div class="mb-3">
                <label for="dimensions" class="form-label">Kích thước:</label>
                <input type="text" class="form-control" id="dimensions" name="dimensions" value="${requestScope.product.dimensions}">
            </div>
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label for="stockQuantity" class="form-label">Số lượng tồn kho:</label>
                    <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" value="${requestScope.product.stockQuantity}" required>
                </div>
                <div class="col-md-4 mb-3">
                    <label for="minStockLevel" class="form-label">Mức tồn kho tối thiểu:</label>
                    <input type="number" class="form-control" id="minStockLevel" name="minStockLevel" value="${requestScope.product.minStockLevel}">
                </div>
                <div class="col-md-4 mb-3">
                    <label for="maxStockLevel" class="form-label">Mức tồn kho tối đa:</label>
                    <input type="number" class="form-control" id="maxStockLevel" name="maxStockLevel" value="${requestScope.product.maxStockLevel}">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" <c:if test="${requestScope.product.active}">checked</c:if>>
                        <label class="form-check-label" for="isActive">Hoạt động</label>
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isFeatured" name="isFeatured" <c:if test="${requestScope.product.featured}">checked</c:if>>
                        <label class="form-check-label" for="isFeatured">Nổi bật</label>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label for="viewCount" class="form-label">Lượt xem:</label>
                    <input type="number" class="form-control" id="viewCount" name="viewCount" value="${requestScope.product.viewCount}">
                </div>
                <div class="col-md-4 mb-3">
                    <label for="salesCount" class="form-label">Lượt bán:</label>
                    <input type="number" class="form-control" id="salesCount" name="salesCount" value="${requestScope.product.salesCount}">
                </div>
                <div class="col-md-4 mb-3">
                    <label for="averageRating" class="form-label">Đánh giá trung bình:</label>
                    <input type="number" class="form-control" id="averageRating" name="averageRating" step="0.01" value="${requestScope.product.averageRating}">
                </div>
            </div>
            <div class="mb-3">
                <label for="reviewCount" class="form-label">Số lượng đánh giá:</label>
                <input type="number" class="form-control" id="reviewCount" name="reviewCount" value="${requestScope.product.reviewCount}">
            </div>

            <button type="submit" class="btn btn-primary w-100"><c:choose><c:when test="${not empty requestScope.product}">Cập nhật Sản phẩm</c:when><c:otherwise>Thêm Sản phẩm</c:otherwise></c:choose></button>
        </form>
        <c:if test="${not empty requestScope.product}">
            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/admin/product-images?productId=${requestScope.product.productID}" class="btn btn-info text-white"><i class="fas fa-images"></i> Quản lý Hình ảnh Sản phẩm</a>
            </div>
        </c:if>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Quay lại danh sách sản phẩm</a>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />