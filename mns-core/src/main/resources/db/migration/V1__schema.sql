DROP TABLE IF EXISTS SettledBet;
DROP TABLE IF EXISTS BetAction_properties;
DROP TABLE IF EXISTS BetAction_tags;
DROP TABLE IF EXISTS BetAction;
DROP TABLE IF EXISTS Property;
DROP TABLE IF EXISTS RunnerPrices_prices;
DROP TABLE IF EXISTS RunnerPrices;
DROP TABLE IF EXISTS MarketPrices;
DROP TABLE IF EXISTS Market_runners;
DROP TABLE IF EXISTS Market;

CREATE TABLE BetAction (
  id            INTEGER          NOT NULL AUTO_INCREMENT,
  actionDate    DATETIME         NOT NULL,
  betActionType INTEGER          NOT NULL,
  betId         VARCHAR(255) UNIQUE,
  amount        DOUBLE PRECISION NOT NULL,
  price         DOUBLE PRECISION NOT NULL,
  side     INTEGER          NOT NULL,
  selectionId   INTEGER          NOT NULL,
  market_id VARCHAR(255) NOT NULL,
  marketPrices_id INTEGER,
  PRIMARY KEY (id));

CREATE TABLE BetAction_properties (
  betAction_id   INTEGER NOT NULL,
  properties     VARCHAR(255),
  properties_KEY VARCHAR(255),
  PRIMARY KEY (betAction_id, properties_KEY));


CREATE TABLE BetAction_tags (
  betAction_id INTEGER NOT NULL,
  tags VARCHAR(255) NOT NULL,
  PRIMARY KEY (betAction_id, tags));

CREATE TABLE Property (
  name  VARCHAR(767)   NOT NULL,
  value VARCHAR(21844) NOT NULL,
  expiryDate DATETIME     NOT NULL,
  PRIMARY KEY (name));

CREATE TABLE Market (
  id            VARCHAR(255) NOT NULL,
  bspMarket     BOOLEAN      NOT NULL,
  competitionId   VARCHAR(255),
  competitionName VARCHAR(255),
  countryCode   VARCHAR(255),
  eventId       VARCHAR(255) NOT NULL,
  eventName     VARCHAR(255) NOT NULL,
  openDate      TIMESTAMP    NOT NULL,
  timezone      VARCHAR(255),
  venue         VARCHAR(255),
  eventTypeId   VARCHAR(255) NOT NULL,
  eventTypeName VARCHAR(255) NOT NULL,
  inPlay        BOOLEAN      NOT NULL,
  matchedAmount DOUBLE,
  name          VARCHAR(255) NOT NULL,
  type          VARCHAR(255),
  version       INTEGER      NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE Market_runners (
  market_id VARCHAR(255) NOT NULL,
  handicap      DOUBLE       NOT NULL,
  name          VARCHAR(255) NOT NULL,
  selectionId   BIGINT       NOT NULL,
  sortPriority  INTEGER      NOT NULL
);

CREATE TABLE MarketPrices (
  id            INTEGER      NOT NULL AUTO_INCREMENT,
  winnerCount   INTEGER      NOT NULL,
  time          DATETIME     NOT NULL,
  market_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id));

CREATE TABLE RunnerPrices (
  id                 INTEGER          NOT NULL AUTO_INCREMENT,
  lastMatchedPrice   DOUBLE PRECISION,
  matchedAmount      DOUBLE PRECISION,
  selectionId        INTEGER          NOT NULL,
  marketPrices_id INTEGER,
  PRIMARY KEY (id));

CREATE TABLE RunnerPrices_prices (
  runnerPrices_id INTEGER          NOT NULL,
  amount             DOUBLE PRECISION NOT NULL,
  price              DOUBLE PRECISION NOT NULL,
  side INTEGER NOT NULL);

CREATE TABLE SettledBet (
  id            INTEGER          NOT NULL AUTO_INCREMENT,
  placed        DATETIME         NOT NULL,
  settled       DATETIME         NOT NULL,
  amount        DOUBLE PRECISION NOT NULL,
  price         DOUBLE PRECISION NOT NULL,
  side     INTEGER          NOT NULL,
  profitAndLoss DOUBLE PRECISION NOT NULL,
  selectionId   INTEGER          NOT NULL,
  selectionName VARCHAR(255)     NOT NULL,
  betAction_id INTEGER NOT NULL,
  PRIMARY KEY (id));

ALTER TABLE Market_runners ADD CONSTRAINT FK_RUNNER_MARKET FOREIGN KEY (market_id) REFERENCES Market (id)
  ON DELETE CASCADE;
ALTER TABLE BetAction ADD CONSTRAINT FK_BA_MARKET FOREIGN KEY (market_id) REFERENCES Market (id)
  ON DELETE CASCADE;
ALTER TABLE BetAction ADD CONSTRAINT FK_BA_MP FOREIGN KEY (marketPrices_id) REFERENCES MarketPrices (id)
  ON DELETE SET NULL;
ALTER TABLE BetAction_properties ADD CONSTRAINT FK_PROP_BA FOREIGN KEY (betAction_id) REFERENCES BetAction (id)
  ON DELETE CASCADE;
ALTER TABLE BetAction_tags ADD CONSTRAINT FK_TAG_BA FOREIGN KEY (betAction_id) REFERENCES BetAction (id)
  ON DELETE CASCADE;
ALTER TABLE MarketPrices ADD CONSTRAINT FK_MP_MARKET FOREIGN KEY (market_id) REFERENCES Market (id)
  ON DELETE CASCADE;
ALTER TABLE RunnerPrices ADD CONSTRAINT FK_RP_MP FOREIGN KEY (marketPrices_id) REFERENCES MarketPrices (id)
  ON DELETE CASCADE;
ALTER TABLE RunnerPrices_prices ADD CONSTRAINT FK_PRICE_RP FOREIGN KEY (runnerPrices_id) REFERENCES RunnerPrices (id)
  ON DELETE CASCADE;
ALTER TABLE SettledBet ADD CONSTRAINT FK_SB_BA FOREIGN KEY (betAction_id) REFERENCES BetAction (id)
  ON DELETE CASCADE;
CREATE INDEX MARKET_DATE_IDX ON Market (openDate);
ALTER TABLE SettledBet ADD CONSTRAINT UK_BA unique (betAction_id);