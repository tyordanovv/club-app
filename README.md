# Project Overview
The project is designed with a focus on clean separation of different domains and loose coupling between them, so it 
would be easier to migrate to microservice architecture in the future. The design follows a layered architecture, 
separating concerns into controllers, services, and persistence layers. By combining security measures such as JWT and 
role-based access control I ensure that specific resources and endpoints stay secured only accepting authenticated
and authorized requests. The project provides a set of RESTful services for authentication, user management (artists and 
clubs), invitation management, and integration with external payment systems (Stripe) and notification system. 
It also uses messaging (RabbitMQ) for asynchronous processing.

***

## Architectural Highlights
### Layered Architecture
1. **Presentation Layer (Controllers):**
REST controllers expose endpoints for various functionalities, including user authentication, artist invitations, event 
management and Stripe account and payment management. Endpoints are secured using Spring Security annotations such as 
*@PreAuthorize* and handle HTTP requests with appropriate HTTP methods (GET, POST, PATCH).

2. **Business Layer (Services):**
Business logic is encapsulated in service classes. These services orchestrate operations between the controllers and 
data repositories, enforce business rules, and handle external integrations. 

3. **Data Access Layer (Repositories & Entities):**
Domain models such as StaffEntity, ClubEntity, and ArtistEntity represent the core data, while repositories provide CRUD
operations against the underlying database.

### Security and Authentication
1. Token-Based Authentication:
The project uses JWT (JSON Web Tokens) to manage stateless authentication. The JWTService interface provides methods to 
generate access and refresh tokens, extract user information from tokens, and validate tokens.

2. Role-Based Access Control:
Some of the endpoints are secured using @PreAuthorize annotations, ensuring that only authenticated users with 
specific roles (ARTIST, CLUB) can access certain resources. 

### Stripe Integration
1. Connected Accounts & KYC:
The application integrates with Stripe to create and manage connected accounts for users.

2. Payment service:

3. Payout service:

4. Webhook Handling: The StripeWebhookController listens for webhook events from Stripe.  Webhook requests are 
validated via signature verification to ensure authenticity.
   * account.updated
   * payment_intent.succeeded
   * payout.paid

### Asynchronous Processing
1. RabbitMQ Messaging - Used to decouples the following processes:
    * Stripe account creation (StripeProducer/StripeConsumer)
    * User notifications
    * Event activation

2. Virtual Threads:

***

## Code Business Logic
### Authentication Flow:

1. When a new registration request is received, the system:
2. Validates the request payload.
3. Checks if the email already exists using StaffService.
4. If the email is new, creates a staff record and generates JWT tokens.
5. Publishes a connected account creation event to RabbitMQ for asynchronous processing.
6. Returns a successful login response with the authentication token.

### Event Generation and Artist Invitation:
TODO DOC

### Artist Invitation handling:
1. Artists can view pending invitations and respond to them. The system ensures that:
2. The current user's identity is correctly determined via CurrentUserService.
3. The invitation response (accept or decline) is processed and the event details are updated accordingly.

### Request generation and payment authorization:
TODO DOC
TODO IMPL

### Request handling (payment capturing):
TODO DOC
TODO IMPL

### Request handling (payment decline):
TODO DOC
TODO IMPL

### Payout request
TODO DOC
TODO IMPL

### Notifications
TODO DOC
TODO IMPL

### Stripe-webhook handling 
1. Verifies the signature of the incoming payload.
2. Routes the event to the corresponding handler:
   * account update: TODO DOC
   * payment success: TODO DOC TODO IMPL

***

## Entities and Relationships

### 1. Staff

#### StaffEntity (Abstract Base Class)
* **Purpose:**
StaffEntity is the abstract base for ClubEntity and ArtistEntity, ensuring a consistent approach to managing staff 
credentials and Stripe details.
* **Key Attributes:**
  * id: Unique identifier.
  * email: Unique email address.
  * password: Encrypted password.
  * role: Enumerated staff role.
  * stripeDetails: Embedded StripeDetails object containing Stripe account info.
  * country: The staff member’s country.
  * createdAt & updatedAt: Timestamps for auditing.
* **Relationships:** 
Parent for both ClubEntity and ArtistEntity through inheritance.

#### ClubEntity
* **Purpose:**
Represents clubs that organize events.
* **Key Attributes:**
  * name: Unique club name.
  * address: Embedded ClubAddress detailing the club’s location.
  * qrCodeIdentifier: A unique identifier for QR code generation.
* **Relationships:**
  * EventEntity: Each event is associated with a club (Many-to-One).
  * ArtistInvitationEntity: Invitations reference a club (Many-to-One).
  * PayoutEntity: Clubs are linked with payouts (Many-to-One).

#### ArtistEntity
* **Purpose:**
Represents artists or DJs who perform at events.
* **Key Attributes:**
  * stageName: Unique identifier for the artist’s public persona.
* **Relationships:**
  * EventEntity: An event’s DJ is an artist (Many-to-One).
  * ArtistInvitationEntity: Invitations reference the artist (Many-to-One).
  * PayoutEntity: Payouts can be directed to an artist (Many-to-One). 

### 2. Event Management and Invitations

#### EventEntity
* **Purpose:**
Models events organized by clubs featuring a DJ and guest requests.
* **Key Attributes:**
  * startTime & endTime: Define the event schedule.
  * isActive: Status flag indicating if the event is currently active.
* **Relationships:**
  * ClubEntity: Each event is hosted by one club (Many-to-One).
  * ArtistEntity: The event’s DJ (Many-to-One).
  * RequestEntity: An event can have multiple guest requests (One-to-Many).

#### ArtistInvitationEntity
* **Purpose:**
Captures invitations sent from clubs to artists for participation in events.
* **Key Attributes:**
  * eventId: Identifies the event associated with the invitation (note: maintained as a field rather than a direct entity relationship).
  * status: Invitation status (using an enumerated type).
  * createdAt & respondedAt: Timestamps for when the invitation was created and responded to.
  * responseMessage: Additional message provided upon response.
* **Relationships:**
  * ClubEntity: The invitation is issued by a specific club (Many-to-One).
  * ArtistEntity: The invitation is targeted to a particular artist (Many-to-One). 

### 3. Guest Requests

#### RequestEntity
* **Purpose:**
Represents requests from guests during an event, such as song requests or greetings.
* **Key Attributes:**
   type: Enumerated to indicate if the request is a SONG or GREETING.
   songTitle & greetingMessage: Specific details depending on the request type.
   guestEmail: Email address of the guest making the request.
* **Relationships:**
   EventEntity: Each request is linked to an event (Many-to-One).
   PaymentEntity: Each request is associated with one payment (One-to-One relationship).

### 4. Payments, and Payouts

#### PaymentEntity
* **Purpose:**
Captures payment information associated with a guest request.
* **Key Attributes:**
  * amount: Payment amount.
  * stripePaymentIntentId: Unique identifier from Stripe.
  * timestamp: When the payment was made.
* **Relationships:**
  * RequestEntity: Directly linked with a request (One-to-One, bidirectional).

#### PayoutEntity
* **Purpose:**
Records payout transactions, either to an artist (as a DJ) or to a club.
* **Key Attributes:**
  * amount: The payout amount.
  * stripePayoutId: Unique identifier from Stripe.
  * status: Payout status (e.g., PENDING, COMPLETED).
* **Relationships:**
  * StaffEntity: Payout are associated with a staff one the user either club or artist requests a payout (Many-to-One).

### 5. Embeddable Value Objects

#### ClubAddress
* **Purpose:**
A value object that encapsulates address details for a club.
* **Key Attributes:**
  * city and street: Represent the location.

#### StripeDetails
* **Purpose:**
Stores Stripe-related account information for staff members.
* **Key Attributes:**
   accountId: Unique Stripe account identifier.
   kycStatus: Enum value indicating the KYC (Know Your Customer) verification status.

***

Problem: Ensure that each user get a stripe connect account
Solution: rabbitmq

Problem: Managing potentially slow external API calls:
Solution: virtual threads/rabbitmq

Problem: Offer payment functionalities and comply with EU regulations
Solution:

Problem: Ensuring the system gracefully handles errors and external API failures.
Solution: 
