CREATE DATABASE IF NOT EXISTS u_ju DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE u_ju;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) DEFAULT '',
    avatar VARCHAR(255) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    gender TINYINT DEFAULT 0,
    birthday DATE DEFAULT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'PARTICIPANT',
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS organizer_certifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    credit_code VARCHAR(50) NOT NULL,
    business_license VARCHAR(255) NOT NULL,
    company_address VARCHAR(255) DEFAULT '',
    contact_person VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    review_remark VARCHAR(255) DEFAULT '',
    reviewer_id BIGINT DEFAULT NULL,
    reviewed_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    publisher_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    cover_images JSON,
    category VARCHAR(20) NOT NULL,
    tags JSON,
    summary VARCHAR(200) NOT NULL,
    detail TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    registration_deadline DATETIME NOT NULL,
    location_type TINYINT NOT NULL DEFAULT 0,
    province VARCHAR(20) DEFAULT '',
    city VARCHAR(20) DEFAULT '',
    district VARCHAR(20) DEFAULT '',
    address VARCHAR(255) DEFAULT '',
    is_free TINYINT NOT NULL DEFAULT 1,
    min_participants INT DEFAULT 0,
    max_participants INT DEFAULT 0,
    need_review TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 0,
    is_public TINYINT NOT NULL DEFAULT 1,
    contact VARCHAR(100) DEFAULT '',
    refund_policy VARCHAR(255) DEFAULT '',
    is_recommended TINYINT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_is_recommended (is_recommended)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 0,
    sold_quantity INT NOT NULL DEFAULT 0,
    description VARCHAR(255) DEFAULT '',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    ticket_id BIGINT DEFAULT NULL,
    form_data JSON,
    status TINYINT NOT NULL DEFAULT 0,
    ticket_code VARCHAR(50) UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_event_id (event_id),
    INDEX idx_ticket_code (ticket_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    registration_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    ticket_id BIGINT DEFAULT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_method TINYINT DEFAULT NULL,
    pay_time DATETIME DEFAULT NULL,
    transaction_id VARCHAR(64) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    expire_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_expire_at (expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_user_event (user_id, event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    type TINYINT NOT NULL DEFAULT 0,
    is_read TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (username, phone, password, real_name, role, status) VALUES
('admin', '13800000000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Admin', 'ADMIN', 1);
