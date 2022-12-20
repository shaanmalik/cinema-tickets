import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

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

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 20);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 1);

    }

    @Test
    public void testPurchase1Adult1ChildTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                );

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 30);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 2);

    }

    @Test
    public void testPurchase1Adult1Child1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 30);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 2);

    }

    @Test
    public void testPurchase2Adult2Child1InfantTicket() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        );

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 60);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 4);

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

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 60);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 4);

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

        Mockito.verify(ticketPaymentService).makePayment(ACCOUNT_ID, 110);
        Mockito.verify(seatReservationService).reserveSeat(ACCOUNT_ID, 8);

    }


    @Test(expected = InvalidPurchaseException.class)
    public void testPurchase1InfantTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
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
    public void testPurchase1ChildTicketInvalid() {

        ticketService.purchaseTickets(ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        );

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testInvalidAccountId() {

        ticketService.purchaseTickets(INVALID_ACCOUNT_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
        );

    }
}
