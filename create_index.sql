CREATE INDEX feed_items_search_idx
  ON feed_items USING GIN (to_tsvector('english', title || ' ' || description));