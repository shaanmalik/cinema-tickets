import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    @Mock
    TicketPaymentService ticketPaymentService;

    @Mock
    SeatReservationService seatReservationService;

    @InjectMocks
    TicketServiceImpl ticketService;

    private static final long ACCOUNT_ID = 1L;
    private static final long INVALID_ACCOUNT_ID = -1L;

    @Test
    public void testPurchase1AdultTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 20);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 1);

    }

    @Test
    public void testPurchase1Adult1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 20);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 1);

    }

    @Test
    public void testPurchase1Infant1AdultTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 20);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 1);

    }

    @Test
    public void testPurchase1Adult1ChildTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 30);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 2);

    }

    @Test
    public void testPurchase1Adult1Child1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 30);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 2);

    }

    @Test
    public void testPurchase2Adult2Child1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 60);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 4);

    }


    @Test
    public void testPurchase1Adult1Adult1Child1Child1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 60);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 4);

    }

    @Test
    public void testPurchaseMixedTickets() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 110);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 8);

    }


    @Test(expected = InvalidPurchaseException.class)
    public void testPurchase1InfantTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchase1ChildTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchase1Infant1ChildTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseNegativeAdultTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseZeroAdultTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInvalidAccountId() {

        ticketService.purchaseTickets(INVALID_ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testTooManyTicketsInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testTooManyAdultAndChildTicketsInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 15),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testTooManyAdultChildAndInfantTicketsInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 15)
        );

    }

    @Test
    public void testAdultChildAndInfantTicketsUnderLimitValid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10)
        );

        verify(ticketPaymentService).makePayment(ACCOUNT_ID, 150);
        verify(seatReservationService).reserveSeat(ACCOUNT_ID, 10);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testUnderTicketLimitButWithNegativeTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 6),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10)
        );

    }

    @Test
    public void testNoTicketsPurchasedNoPaymentMadeNoSeatsReserved() {

        ticketService.purchaseTickets(ACCOUNT_ID);

        verifyNoInteractions(ticketPaymentService);
        verifyNoInteractions(seatReservationService);

    }

}
