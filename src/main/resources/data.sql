insert into product(name, price, image_url, validated)
values ('아이스 카페 아메리카노 T', 4700, 'https://st.kakaocdn.net/product/gift/product/20231010111814_9a667f9eccc943648797925498bdd8a3.jpg', true);

insert into member(email, password, authority)
values ('admin@kakao.com', '$2a$10$KvNzwiNd2I9MAYC/ix4A.et4UjQ/nOff7Y6jl9l6UVie5Gk7UyKfO', 'ROLE_ADMIN'),
         ('md@kakao.com', '$2a$10$KvNzwiNd2I9MAYC/ix4A.et4UjQ/nOff7Y6jl9l6UVie5Gk7UyKfO', 'ROLE_MD'),
         ('seller@kakao.com', '$2a$10$KvNzwiNd2I9MAYC/ix4A.et4UjQ/nOff7Y6jl9l6UVie5Gk7UyKfO', 'ROLE_SELLER');