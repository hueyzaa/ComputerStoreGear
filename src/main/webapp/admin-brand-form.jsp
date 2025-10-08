<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4"><c:choose><c:when test="${not empty requestScope.brand}">Sửa Thương hiệu</c:when><c:otherwise>Thêm Thương hiệu mới</c:otherwise></c:choose></h1>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/admin/brands/<c:choose><c:when test="${not empty requestScope.brand}">edit</c:when><c:otherwise>add</c:otherwise></c:choose>" method="post">
            <c:if test="${not empty requestScope.brand}">
                <input type="hidden" name="brandId" value="${requestScope.brand.brandID}">
            </c:if>

            <div class="mb-3">
                <label for="brandName" class="form-label">Tên Thương hiệu:</label>
                <input type="text" class="form-control" id="brandName" name="brandName" value="${requestScope.brand.brandName}" required>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô tả:</label>
                <textarea class="form-control" id="description" name="description" rows="3">${requestScope.brand.description}</textarea>
            </div>
            <div class="mb-3">
                <label for="logoURL" class="form-label">URL Logo:</label>
                <input type="text" class="form-control" id="logoURL" name="logoURL" value="${requestScope.brand.logoURL}">
            </div>
            <div class="mb-3">
                <label for="website" class="form-label">Website:</label>
                <input type="text" class="form-control" id="website" name="website" value="${requestScope.brand.website}">
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" <c:if test="${requestScope.brand.active}">checked</c:if>>
                <label class="form-check-label" for="isActive">Hoạt động</label>
            </div>

            <button type="submit" class="btn btn-primary w-100"><c:choose><c:when test="${not empty requestScope.brand}">Cập nhật Thương hiệu</c:when><c:otherwise>Thêm Thương hiệu</c:otherwise></c:choose></button>
        </form>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/admin/brands" class="btn btn-secondary">Quay lại danh sách thương hiệu</a>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />