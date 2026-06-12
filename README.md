#Private Tutoring#
A web-based application for managing private tutoring services — connecting students/parents with tutors, and helping manage tutoring sessions, schedules, and related data.

📖 About
Private Tutoring is a full-stack web application designed to streamline the process of arranging and managing private tutoring. The system provides a platform where users can browse tutors, schedule sessions, and manage tutoring-related information through a dedicated backend service, frontend interface, and relational database.

🛠️ Tech Stack
This project is organized into three main components:
- Frontend — Built with TypeScript (web client interface)
- Backend Server — Built with Java (REST API server)
- DatabaseSQL — SQL scripts for database schema and seed data
- RD Final.pdf — Entity Relationship Diagram describing the database design

📂 Project Structure
Private-Tutoring/
├── Backend Server/     # Java-based backend API
├── DatabaseSQL/        # Database schema & SQL scripts
├── Frontend/           # TypeScript-based frontend application
├── ERD Final.pdf       # Entity Relationship Diagram
├── package-lock.json
└── README.md

🚀 Getting Started
Prerequisites
Make sure you have the following installed on your machine:
- Node.js (for the Frontend)
- Java JDK (for the Backend Server)
- A relational database (e.g. MySQL/PostgreSQL — see DatabaseSQL/ for the schema)
- A package manager such as npm or yarn

- 1. Clone the Repository

git clone https://github.com/brynwlsn/Private-Tutoring.git
cd Private-Tutoring

2. Set Up the Database
Please run all of the query, create database, create table, and insert dummy

3. Set Up the Backend Server
Please Run the Java Backend

4. Set Up the Frontend
First locate the file on terminal
cd frontend
npm install
npm run dev
then copy the link
The frontend will typically be available at http://localhost:3000 (or another port depending on configuration).

✨ Features
Update this section with the actual features implemented in the application.
- User authentication (students/parents & tutors)
- Tutor profile management
- Session/schedule booking
- Course or subject management
- Admin dashboard for managing data


📐 Database Design
The database schema and relationships are documented in ERD Final.pdf, and the corresponding SQL scripts can be found in the DatabaseSQL/ folder.
