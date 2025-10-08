<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Hình ảnh cho Sản phẩm ID: ${requestScope.productId}</h1>

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

    <h2 class="mb-3">Hình ảnh hiện có</h2>
    <c:if test="${empty requestScope.images}">
        <div class="alert alert-info text-center" role="alert">
            Chưa có hình ảnh nào cho sản phẩm này.
        </div>
    </c:if>
    <div class="row image-grid g-3">
        <c:forEach var="image" items="${requestScope.images}">
            <div class="col-md-3 col-sm-4 col-6">
                <div class="card image-card shadow-sm h-100">
                    <img src="${pageContext.request.contextPath}/static/images/${image.imageURL.substring(image.imageURL.lastIndexOf('/') + 1)}" class="card-img-top" alt="${image.altText}">
                    <div class="card-body text-center">
                        <p class="card-text mb-1"><strong>Alt Text:</strong> ${image.altText}</p>
                        <p class="card-text mb-1"><strong>Thứ tự hiển thị:</strong> ${image.displayOrder}</p>
                        <p class="card-text mb-2"><strong>Hình ảnh chính:</strong> <c:choose><c:when test="${image.mainImage}"><span class="badge bg-success">Có</span></c:when><c:otherwise><span class="badge bg-secondary">Không</span></c:otherwise></c:choose></p>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/admin/product-images/delete?id=${image.imageID}&productId=${requestScope.productId}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa hình ảnh này không?');"><i class="fas fa-trash"></i> Xóa</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <h2 class="mt-5 mb-3">Tải lên hình ảnh mới</h2>
    <div class="card p-4 shadow-sm upload-form">
        <form action="${pageContext.request.contextPath}/admin/product-images/upload" method="post" enctype="multipart/form-data">
            <input type="hidden" name="productId" value="${requestScope.productId}">
            <div class="mb-3">
                <label for="imageFile" class="form-label">Chọn tệp hình ảnh:</label>
                <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*" required>
            </div>
            <div class="mb-3">
                <label for="altText" class="form-label">Văn bản thay thế (Alt Text):</label>
                <input type="text" class="form-control" id="altText" name="altText" placeholder="Mô tả hình ảnh">
            </div>
            <div class="mb-3">
                <label for="displayOrder" class="form-label">Thứ tự hiển thị:</label>
                <input type="number" class="form-control" id="displayOrder" name="displayOrder" value="0">
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="isMainImage" name="isMainImage">
                <label class="form-check-label" for="isMainImage">Đặt làm hình ảnh chính</label>
            </div>
            <button type="submit" class="btn btn-success"><i class="fas fa-upload"></i> Tải lên hình ảnh</button>
        </form>
    </div>

    <div class="text-center mt-4">
        <a href="${pageContext.request.contextPath}/admin/products/edit?id=${requestScope.productId}" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại trang sửa sản phẩm</a>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />