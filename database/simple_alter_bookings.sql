-- =====================================================
-- SIMPLE ALTER SCRIPT FOR BOOKINGS TABLE
-- Thêm trường UpdatedAt và Description
-- =====================================================

-- Thêm cột UpdatedAt
ALTER TABLE [dbo].[Bookings]
ADD [UpdatedAt] DATETIME2(7) NOT NULL DEFAULT GETDATE();

-- Thêm cột Description
ALTER TABLE [dbo].[Bookings]
ADD [Description] NVARCHAR(1000) NULL;

-- Cập nhật UpdatedAt cho các record hiện có
UPDATE [dbo].[Bookings]
SET [UpdatedAt] = ISNULL([CreatedAt], GETDATE());

-- Kiểm tra kết quả
SELECT TOP 5
    BookingID,
    CustomerID,
    Status,
    Description,
    CreatedAt,
    UpdatedAt
FROM [dbo].[Bookings]
ORDER BY BookingID DESC;
