EXEC sp_configure filestream_access_level, 2;
RECONFIGURE;


CREATE DATABASE ArchiveFT
    ON
    PRIMARY ( NAME = ArchFT,
    FILENAME = 'C:\dataFT\archdatFT.mdf'),
    FILEGROUP FileStreamGroup1 CONTAINS FILESTREAM (
        NAME = Arch3, FILENAME = 'C:\dataFT\filestreamFT')
    LOG ON  ( NAME = ArchlogFT,
    FILENAME = 'C:\dataFT\archlogFT.ldf')
GO


ALTER DATABASE ArchiveFT
    SET FILESTREAM (NON_TRANSACTED_ACCESS = FULL,
    DIRECTORY_NAME = N'archdatFS')
GO

USE ArchiveFT
GO

CREATE TABLE ArchFileTable AS FileTable
GO