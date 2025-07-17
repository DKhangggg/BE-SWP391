-- =====================================================
-- ALTER TABLE SCRIPT FOR BOOKINGS TABLE
-- Thêm trường UpdatedAt và Description vào bảng Bookings
-- Database: Microsoft SQL Server
-- =====================================================

USE [your_database_name]; -- Thay thế bằng tên database thực tế
GO

-- Kiểm tra xem cột UpdatedAt đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Bookings]') AND name = 'UpdatedAt')
BEGIN
    -- Thêm cột UpdatedAt
    ALTER TABLE [dbo].[Bookings]
    ADD [UpdatedAt] DATETIME2(7) NOT NULL DEFAULT GETDATE();
    
    PRINT 'Đã thêm cột UpdatedAt vào bảng Bookings';
END
ELSE
BEGIN
    PRINT 'Cột UpdatedAt đã tồn tại trong bảng Bookings';
END
GO

-- Kiểm tra xem cột Description đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Bookings]') AND name = 'Description')
BEGIN
    -- Thêm cột Description
    ALTER TABLE [dbo].[Bookings]
    ADD [Description] NVARCHAR(1000) NULL;
    
    PRINT 'Đã thêm cột Description vào bảng Bookings';
END
ELSE
BEGIN
    PRINT 'Cột Description đã tồn tại trong bảng Bookings';
END
GO

-- Cập nhật giá trị UpdatedAt cho các bản ghi hiện có (set = CreatedAt)
UPDATE [dbo].[Bookings]
SET [UpdatedAt] = ISNULL([CreatedAt], GETDATE())
WHERE [UpdatedAt] IS NULL OR [UpdatedAt] = '1900-01-01';

PRINT 'Đã cập nhật giá trị UpdatedAt cho các bản ghi hiện có';
GO

-- Tạo trigger để tự động cập nhật UpdatedAt khi có thay đổi
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'TR_Bookings_UpdatedAt')
BEGIN
    DROP TRIGGER [dbo].[TR_Bookings_UpdatedAt];
    PRINT 'Đã xóa trigger cũ TR_Bookings_UpdatedAt';
END
GO

CREATE TRIGGER [dbo].[TR_Bookings_UpdatedAt]
ON [dbo].[Bookings]
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE b
    SET [UpdatedAt] = GETDATE()
    FROM [dbo].[Bookings] b
    INNER JOIN inserted i ON b.BookingID = i.BookingID;
END
GO

PRINT 'Đã tạo trigger TR_Bookings_UpdatedAt để tự động cập nhật UpdatedAt';
GO

-- Tạo index cho cột UpdatedAt để tối ưu performance
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_Bookings_UpdatedAt' AND object_id = OBJECT_ID('[dbo].[Bookings]'))
BEGIN
    CREATE NONCLUSTERED INDEX [IX_Bookings_UpdatedAt]
    ON [dbo].[Bookings] ([UpdatedAt] DESC)
    INCLUDE ([BookingID], [Status]);
    
    PRINT 'Đã tạo index IX_Bookings_UpdatedAt';
END
ELSE
BEGIN
    PRINT 'Index IX_Bookings_UpdatedAt đã tồn tại';
END
GO

-- Kiểm tra kết quả
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Bookings' 
    AND COLUMN_NAME IN ('UpdatedAt', 'Description')
ORDER BY COLUMN_NAME;

PRINT 'Hoàn thành việc thêm các trường mới vào bảng Bookings';
GO

-- =====================================================
-- SAMPLE QUERIES TO TEST THE NEW FIELDS
-- =====================================================

-- Test query 1: Xem các booking được cập nhật gần đây
/*
SELECT TOP 10
    BookingID,
    CustomerID,
    Status,
    Description,
    CreatedAt,
    UpdatedAt,
    DATEDIFF(MINUTE, CreatedAt, UpdatedAt) as MinutesSinceCreated
FROM [dbo].[Bookings]
ORDER BY UpdatedAt DESC;
*/

-- Test query 2: Tìm booking có description
/*
SELECT 
    BookingID,
    CustomerID,
    Status,
    Description,
    UpdatedAt
FROM [dbo].[Bookings]
WHERE Description IS NOT NULL AND Description != ''
ORDER BY UpdatedAt DESC;
*/

-- Test query 3: Thống kê booking theo thời gian cập nhật
/*
SELECT 
    CAST(UpdatedAt AS DATE) as UpdateDate,
    COUNT(*) as BookingCount,
    COUNT(CASE WHEN Description IS NOT NULL THEN 1 END) as BookingsWithDescription
FROM [dbo].[Bookings]
WHERE UpdatedAt >= DATEADD(DAY, -30, GETDATE())
GROUP BY CAST(UpdatedAt AS DATE)
ORDER BY UpdateDate DESC;
*/
