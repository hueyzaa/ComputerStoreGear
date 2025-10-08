DROP DATABASE PCGearStore;
CREATE DATABASE PCGearStore;
USE PCGearStore;
-- SQL Schema for ComputerStore Database

-- Table: Users
CREATE TABLE Users (
                       UserID VARCHAR(36) PRIMARY KEY,
                       Username VARCHAR(50) UNIQUE NOT NULL,
                       Email VARCHAR(100) UNIQUE NOT NULL,
                       PasswordHash VARCHAR(255) NOT NULL,
                       Salt VARCHAR(255) NOT NULL,
                       FirstName VARCHAR(50),
                       LastName VARCHAR(50),
                       PhoneNumber VARCHAR(20),
                       DateOfBirth DATE,
                       Gender VARCHAR(10),
                       IsEmailVerified BIT DEFAULT 0,
                       IsActive BIT DEFAULT 1,
                       Role VARCHAR(20) DEFAULT 'user',
                       CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                       ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                       LastLoginDate DATETIME
);

-- Table: Brands
CREATE TABLE Brands (
                        BrandID VARCHAR(36) PRIMARY KEY,
                        BrandName VARCHAR(100) UNIQUE NOT NULL,
                        Description TEXT,
                        LogoURL VARCHAR(255),
                        Website VARCHAR(255),
                        IsActive BIT DEFAULT 1,
                        CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table: Categories
CREATE TABLE Categories (
                            CategoryID VARCHAR(36) PRIMARY KEY,
                            CategoryName VARCHAR(100) UNIQUE NOT NULL,
                            Description TEXT,
                            ParentCategoryID VARCHAR(36),
                            IsActive BIT DEFAULT 1,
                            CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                            ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (ParentCategoryID) REFERENCES Categories(CategoryID)
);

-- Table: Products
CREATE TABLE Products (
                          ProductID VARCHAR(36) PRIMARY KEY,
                          ProductName VARCHAR(255) NOT NULL,
                          SKU VARCHAR(100) UNIQUE,
                          CategoryID VARCHAR(36) NOT NULL,
                          BrandID VARCHAR(36),
                          Description TEXT,
                          ShortDescription VARCHAR(500),
                          Price DECIMAL(18, 2) NOT NULL,
                          ComparePrice DECIMAL(18, 2),
                          CostPrice DECIMAL(18, 2),
                          Weight DECIMAL(10, 2),
                          Dimensions VARCHAR(100),
                          StockQuantity INT DEFAULT 0,
                          MinStockLevel INT DEFAULT 0,
                          MaxStockLevel INT DEFAULT 0,
                          IsActive BIT DEFAULT 1,
                          IsFeatured BIT DEFAULT 0,
                          ViewCount INT DEFAULT 0,
                          SalesCount INT DEFAULT 0,
                          AverageRating DECIMAL(3, 2) DEFAULT 0.0,
                          ReviewCount INT DEFAULT 0,
                          CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                          ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
                          FOREIGN KEY (BrandID) REFERENCES Brands(BrandID)
);

-- Table: ProductImages
CREATE TABLE ProductImages (
                               ImageID VARCHAR(36) PRIMARY KEY,
                               ProductID VARCHAR(36) NOT NULL,
                               ImageURL VARCHAR(255) NOT NULL,
                               AltText VARCHAR(255),
                               DisplayOrder INT DEFAULT 0,
                               IsMainImage BIT DEFAULT 0,
                               CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Table: ProductReviews
CREATE TABLE ProductReviews (
                                ReviewID VARCHAR(36) PRIMARY KEY,
                                ProductID VARCHAR(36) NOT NULL,
                                UserID VARCHAR(36) NOT NULL,
                                Rating INT NOT NULL CHECK (Rating >= 1 AND Rating <= 5),
                                Title VARCHAR(255),
                                ReviewText TEXT,
                                IsVerifiedPurchase BIT DEFAULT 0,
                                IsPublished BIT DEFAULT 0,
                                HelpfulCount INT DEFAULT 0,
                                CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
                                FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Table: ProductSpecifications
CREATE TABLE ProductSpecifications (
                                       SpecID VARCHAR(36) PRIMARY KEY,
                                       ProductID VARCHAR(36) NOT NULL,
                                       SpecName VARCHAR(100) NOT NULL,
                                       SpecValue VARCHAR(255) NOT NULL,
                                       SpecGroup VARCHAR(100),
                                       DisplayOrder INT DEFAULT 0,
                                       FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Table: ShoppingCarts
CREATE TABLE ShoppingCarts (
                               CartID VARCHAR(36) PRIMARY KEY,
                               UserID VARCHAR(36) NOT NULL,
                               ProductID VARCHAR(36) NOT NULL,
                               Quantity INT NOT NULL CHECK (Quantity > 0),
                               AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                               ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (UserID) REFERENCES Users(UserID),
                               FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Table: Wishlists
CREATE TABLE Wishlists (
                           WishlistID VARCHAR(36) PRIMARY KEY,
                           UserID VARCHAR(36) NOT NULL,
                           ProductID VARCHAR(36) NOT NULL,
                           AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (UserID) REFERENCES Users(UserID),
                           FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Table: UserAddresses
CREATE TABLE UserAddresses (
                               AddressID VARCHAR(36) PRIMARY KEY,
                               UserID VARCHAR(36) NOT NULL,
                               AddressType VARCHAR(50) NOT NULL, -- e.g., 'shipping', 'billing', 'home'
                               AddressLine1 VARCHAR(255) NOT NULL,
                               AddressLine2 VARCHAR(255),
                               City VARCHAR(100) NOT NULL,
                               State VARCHAR(100),
                               PostalCode VARCHAR(20),
                               Country VARCHAR(100) NOT NULL,
                               IsDefault BIT DEFAULT 0,
                               CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Table: Orders
CREATE TABLE Orders (
                        OrderID VARCHAR(36) PRIMARY KEY,
                        OrderNumber VARCHAR(50) UNIQUE NOT NULL,
                        UserID VARCHAR(36) NOT NULL,
                        OrderStatus VARCHAR(50) NOT NULL, -- e.g., 'Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled'
                        PaymentStatus VARCHAR(50) NOT NULL, -- e.g., 'Pending', 'Paid', 'Refunded'
                        PaymentMethod VARCHAR(50),
                        PaymentTransactionID VARCHAR(255),
                        SubtotalAmount DECIMAL(18, 2) NOT NULL,
                        TaxAmount DECIMAL(18, 2) DEFAULT 0.0,
                        ShippingAmount DECIMAL(18, 2) DEFAULT 0.0,
                        DiscountAmount DECIMAL(18, 2) DEFAULT 0.0,
                        TotalAmount DECIMAL(18, 2) NOT NULL,
                        CurrencyCode VARCHAR(10) DEFAULT 'VND',
                        ShippingAddressID VARCHAR(36),
                        BillingAddressID VARCHAR(36),
                        ShippingTrackingNumber VARCHAR(100),
                        ShippingCarrier VARCHAR(100),
                        Notes TEXT,
                        OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                        ShippedDate DATETIME,
                        DeliveredDate DATETIME,
                        ModifiedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (UserID) REFERENCES Users(UserID),
                        FOREIGN KEY (ShippingAddressID) REFERENCES UserAddresses(AddressID),
                        FOREIGN KEY (BillingAddressID) REFERENCES UserAddresses(AddressID)
);

-- Table: OrderItems
CREATE TABLE OrderItems (
                            OrderItemID VARCHAR(36) PRIMARY KEY,
                            OrderID VARCHAR(36) NOT NULL,
                            ProductID VARCHAR(36) NOT NULL,
                            ProductName VARCHAR(255) NOT NULL, -- Denormalized for historical record
                            SKU VARCHAR(100), -- Denormalized
                            Quantity INT NOT NULL CHECK (Quantity > 0),
                            UnitPrice DECIMAL(18, 2) NOT NULL,
                            TotalPrice DECIMAL(18, 2) NOT NULL,
                            FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
                            FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Table: Coupons
CREATE TABLE Coupons (
                         CouponID VARCHAR(36) PRIMARY KEY,
                         CouponCode VARCHAR(50) UNIQUE NOT NULL,
                         CouponName VARCHAR(100),
                         Description TEXT,
                         DiscountType VARCHAR(20) NOT NULL, -- e.g., 'percentage', 'fixed_amount'
                         DiscountValue DECIMAL(18, 2) NOT NULL,
                         MinOrderAmount DECIMAL(18, 2),
                         MaxDiscountAmount DECIMAL(18, 2),
                         UsageLimit INT, -- Total usage limit for the coupon
                         UsedCount INT DEFAULT 0,
                         IsActive BIT DEFAULT 1,
                         StartDate DATETIME NOT NULL,
                         EndDate DATETIME NOT NULL,
                         CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table: UserCouponUsages
CREATE TABLE UserCouponUsages (
                                  UsageID VARCHAR(36) PRIMARY KEY,
                                  UserID VARCHAR(36) NOT NULL,
                                  CouponID VARCHAR(36) NOT NULL,
                                  OrderID VARCHAR(36), -- Nullable if coupon can be applied without immediate order (e.g., saved for later)
                                  UsedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (UserID) REFERENCES Users(UserID),
                                  FOREIGN KEY (CouponID) REFERENCES Coupons(CouponID),
                                  FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

-- Table: EmailVerificationTokens
CREATE TABLE EmailVerificationTokens (
                                         TokenID VARCHAR(36) PRIMARY KEY,
                                         UserID VARCHAR(36) NOT NULL,
                                         Token VARCHAR(36) UNIQUE NOT NULL, -- The actual token string
                                         TokenType VARCHAR(50) NOT NULL, -- e.g., 'email_verification', 'password_reset'
                                         ExpiryDate DATETIME NOT NULL,
                                         IsUsed BIT DEFAULT 0,
                                         CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         UsedDate DATETIME,
                                         FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Table: InventoryTransactions
CREATE TABLE InventoryTransactions (
                                       TransactionID VARCHAR(36) PRIMARY KEY,
                                       ProductID VARCHAR(36) NOT NULL,
                                       TransactionType VARCHAR(50) NOT NULL, -- e.g., 'in', 'out', 'adjustment'
                                       Quantity INT NOT NULL,
                                       ReferenceType VARCHAR(50), -- e.g., 'order', 'return', 'manual'
                                       ReferenceID VARCHAR(36), -- ID of the related order, return, etc.
                                       Notes TEXT,
                                       CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       CreatedBy VARCHAR(36), -- UserID who performed the transaction
                                       FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
                                       FOREIGN KEY (CreatedBy) REFERENCES Users(UserID)
);

SELECT * FROM Users
DELETE FROM Users WHERE Username LIKE'demouser';
SELECT * FROM EmailVerificationTokens
DELETE FROM EmailVerificationTokens
SELECT * FROM Brands
UPDATE Users SET Role='admin' WHERE Username LIKE 'admin';
DELETE FROM ProductImages;
DELETE FROM ProductReviews;
DELETE FROM ProductSpecifications;
DELETE FROM ShoppingCarts;
DELETE FROM Wishlists;
DELETE FROM OrderItems;
DELETE FROM Orders;
DELETE FROM UserCouponUsages;
DELETE FROM Coupons;
DELETE FROM EmailVerificationTokens;
DELETE FROM InventoryTransactions;
DELETE FROM UserAddresses;
DELETE FROM Products;
DELETE FROM Categories;
DELETE FROM Brands;
DELETE FROM Users;


SELECT * FROM Brands
SELECT * FROM Orders
SELECT * FROM ShoppingCarts
SELECT * FROM [dbo].[ProductReviews]

SELECT * FROM ProductReviews WHERE ProductID = 'cc18fbbf-b62c-4016-af7f-69cf3b45039f' AND IsPublished = 1;
SELECT pr.ReviewID, pr.ProductID, pr.UserID, pr.Rating, pr.Title, pr.ReviewText,
       pr.IsVerifiedPurchase, pr.IsPublished, pr.HelpfulCount, pr.CreatedDate,
       u.Username, u.FirstName, u.LastName
FROM ProductReviews pr
         JOIN Users u ON pr.UserID = u.UserID
WHERE pr.ProductID = 'cc18fbbf-b62c-4016-af7f-69cf3b45039f' AND pr.IsPublished = 1
ORDER BY pr.CreatedDate DESC;