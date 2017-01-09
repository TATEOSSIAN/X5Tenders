IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[TenderList]') AND type in (N'U'))
  BEGIN
    CREATE TABLE [dbo].[TenderList](
      [ID] [int] IDENTITY(1,1) NOT NULL,
      [Number] [nvarchar](50) NULL,
      [Description] [nvarchar](400) NULL,
      [StartReq] [datetime2](0) NULL,
      [Start] [datetime2](0) NULL,
      [Finish] [datetime2](0) NULL,
      CONSTRAINT [PK__TenderLi__3214EC2727DCCFDB] PRIMARY KEY CLUSTERED
        (
          [ID] ASC
        )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
    ) ON [PRIMARY]
  END