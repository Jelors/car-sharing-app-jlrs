delete from payments;
delete from rentals;
delete from users;

insert into payments (id, rental_id, session_url, session_id, total, status, type, is_deleted)
VALUES (1, 1,
        'https://checkout.stripe.com/c/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0#fidkdWxOYHwnPyd1blpxYHZxWjA0S1N3YHV3fGZka3ZpYmZpYHV3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3',
        'cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', 2000, 'PAID', 'PAYMENT', 0),
       (2, 4,
        'https://checkout.stripe.com/c/pay/cs_test_B9876543210asdfghjklqwertyuiopzxcvbnm123#fidkdWxOYHwnPyd1blpxYHZxWjA0S1N3YHV3fGZka3ZpYmZpYHV3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3Z2p2Y3NgY2B3',
        'cs_test_B9876543210asdfghjklqwertyuiopzxcvbnm123', 4600, 'PAID', 'PAYMENT', 0);