ALTER TABLE SettledBet ADD matched DATETIME NOT NULL after placed;
ALTER TABLE SettledBet MODIFY placed DATETIME;