--
-- Numbers
--

-- STQuantity
-- Standard Type QUANTITY
-- QUANTITY * PRICE = AMOUNT or QUANTITY * PRICE = QUANTITY
IF EXISTS(SELECT 1 FROM systypes WHERE name='STQuantity') THEN
   DROP DATATYPE "STQuantity"
END IF
go

CREATE DATATYPE "STQuantity" NUMERIC(16,6)
go

-- STPrice
-- Standard Type PRICE
-- QUANTITY * PRICE = AMOUNT or QUANTITY * PRICE = QUANTITY
IF EXISTS(SELECT 1 FROM systypes WHERE name='STPrice') THEN
   DROP DATATYPE "STPrice"
END IF
go

CREATE DATATYPE "STPrice" NUMERIC(16,8)
go

-- STAmount
-- Standard Type AMOUNT
-- QUANTITY * PRICE = AMOUNT or QUANTITY * PRICE = QUANTITY
IF EXISTS(SELECT 1 FROM systypes WHERE name='STAmount') THEN
   DROP DATATYPE "STAmount"
END IF
go

CREATE DATATYPE "STAmount" NUMERIC(16,2)
go

-- STPercent
-- Standard Type PERCENT
IF EXISTS(SELECT 1 FROM systypes WHERE name='STPercent') THEN
   DROP DATATYPE "STPercent"
END IF
go

CREATE DATATYPE "STPercent" NUMERIC(7,4)
go

-- STInteger
-- Standard Type INTEGER
IF EXISTS(SELECT 1 FROM systypes WHERE name='STInteger') THEN
   DROP DATATYPE "STInteger"
END IF
go

CREATE DATATYPE "STInteger" INTEGER NOT NULL;
go

-- STParameter
-- Standard Type DECIMAL_064
-- A double precision approximate numeric type in the database; Double or BigDecimal in Java
IF EXISTS(SELECT 1 FROM systypes WHERE name='STParameter') THEN
   DROP DATATYPE "STParameter"
END IF
go

CREATE DATATYPE "STParameter" DOUBLE
go


--
-- Characters
--

-- STString1
-- Standard Type STRING_1
IF EXISTS(SELECT 1 FROM systypes WHERE name='STString1') THEN
   DROP DATATYPE "STString1"
END IF
go

CREATE DATATYPE "STString1" VARCHAR(1)
go

-- STString3
-- Standard Type STRING_3
IF EXISTS(SELECT 1 FROM systypes WHERE name='STString3') THEN
   DROP DATATYPE "STString3"
END IF
go

CREATE DATATYPE "STString3" VARCHAR(3)
go

-- STString9
-- Standard Type STRING_9
IF EXISTS(SELECT 1 FROM systypes WHERE name='STString9') THEN
   DROP DATATYPE "STString9"
END IF
go

CREATE DATATYPE "STString9" VARCHAR(9)
go

-- STStringM
-- Standard Type STRING_M
-- Character data with more than 128 bytes are stored differently in Sybase ASA
IF EXISTS(SELECT 1 FROM systypes WHERE name='STStringM') THEN
   DROP DATATYPE "STStringM"
END IF
go

CREATE DATATYPE "STStringM" VARCHAR(128)
go

-- STText
-- Standard Type TEXT
-- Unlimited character data
IF EXISTS(SELECT 1 FROM systypes WHERE name='STText') THEN
   DROP DATATYPE "STText"
END IF
go

CREATE DATATYPE "STText" LONG VARCHAR
go


--
-- Date & Time
--

-- STDate
-- Standard Type DATE
IF EXISTS(SELECT 1 FROM systypes WHERE name='STDate') THEN
   DROP DATATYPE "STDate"
END IF
go

CREATE DATATYPE "STDate" DATE
go

-- STTime
-- Standard Type TIME
IF EXISTS(SELECT 1 FROM systypes WHERE name='STTime') THEN
   DROP DATATYPE "STTime"
END IF
go

CREATE DATATYPE "STTime" TIME
go

-- STDateTime
-- Standard Type DATETIME as in DATE & TIME
IF EXISTS(SELECT 1 FROM systypes WHERE name='STDateTime') THEN
   DROP DATATYPE "STDateTime"
END IF
go

CREATE DATATYPE "STDateTime" TIMESTAMP
go


--
-- Logical
--

-- STBoolean
-- Standard Type BOOLEAN
IF EXISTS(SELECT 1 FROM systypes WHERE name='STBoolean') THEN
   DROP DATATYPE "STBoolean"
END IF
go

CREATE DATATYPE "STBoolean" BIT
go


--
-- Other
--

-- STColour
-- Colour as a hex string
IF EXISTS(SELECT 1 FROM systypes WHERE name='STColour') THEN
   DROP DATATYPE "STColour"
END IF
go

CREATE DATATYPE "STColour" VARCHAR(7)
go
