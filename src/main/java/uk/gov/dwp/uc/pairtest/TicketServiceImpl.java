package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;
import java.util.Map;


public class TicketServiceImpl implements TicketService {

    private static final int MAX_TICKETS = 20;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    private final Map<TicketTypeRequest.Type, Integer> ticketTypeToPriceMap;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
        ticketTypeToPriceMap = getTicketTypeToPriceMap();
    }

    private Map<TicketTypeRequest.Type, Integer> getTicketTypeToPriceMap() {
        Map<TicketTypeRequest.Type, Integer> ticketTypeToPriceMap = new HashMap<>();
        ticketTypeToPriceMap.put(TicketTypeRequest.Type.ADULT, 20);
        ticketTypeToPriceMap.put(TicketTypeRequest.Type.CHILD, 10);
        return ticketTypeToPriceMap;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if (ticketTypeRequests.length == 0) {
            return;
        }

        if (!(accountId > 0)) {
            // only accountIds greater than 0 are valid
            throw new InvalidPurchaseException();
        }

        int totalTicketsPurchased = 0;

        int totalSeats = 0;
        int totalAmountToPay = 0;

        int totalAdultTickets = 0;
        int totalChildTickets = 0;
        int totalInfantTickets = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {

            totalTicketsPurchased += ticketTypeRequest.getNoOfTickets();

            if (ticketTypeRequest.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException();
            }

            switch (ticketTypeRequest.getTicketType()) {
                case ADULT:
                    totalAdultTickets += ticketTypeRequest.getNoOfTickets();
                    break;
                case CHILD:
                    totalChildTickets += ticketTypeRequest.getNoOfTickets();
                    break;
                case INFANT:
                    totalInfantTickets += ticketTypeRequest.getNoOfTickets();
                    break;
            }

            if (totalTicketsPurchased > MAX_TICKETS) {
                throw new InvalidPurchaseException();
            }

            if (!TicketTypeRequest.Type.INFANT.equals(ticketTypeRequest.getTicketType())) {
                totalSeats += ticketTypeRequest.getNoOfTickets();
                totalAmountToPay += ticketTypeRequest.getNoOfTickets() * ticketTypeToPriceMap.get(ticketTypeRequest.getTicketType());
            }
        }

        if (totalChildTickets + totalInfantTickets > 0 && totalAdultTickets == 0) {
            // cannot purchase child or infant tickets without at least 1 adult ticket
            throw new InvalidPurchaseException();
        }

        seatReservationService.reserveSeat(accountId, totalSeats);
        ticketPaymentService.makePayment(accountId, totalAmountToPay);
    }

}
