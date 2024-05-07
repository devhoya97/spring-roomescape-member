package roomescape.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.domain.ReservatorName;
import roomescape.dto.request.ReservationAddRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ThemeResponse;
import roomescape.exceptions.ClientException;
import roomescape.repository.reservation.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeService reservationTimeService, ThemeService themeService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    public ReservationResponse addReservation(ReservationAddRequest reservationAddRequest) {
        ReservationTimeResponse timeResponse = reservationTimeService.getTime(reservationAddRequest.timeId());
        ThemeResponse themeResponse = themeService.getTheme(reservationAddRequest.themeId());

        Reservation reservation = new Reservation(
                new ReservatorName(reservationAddRequest.name()),
                reservationAddRequest.date(),
                timeResponse.toReservationTime(),
                themeResponse.toTheme()
        );

        validateIsBeforeNow(reservation);
        validateIsDuplicated(reservation);

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private void validateIsBeforeNow(Reservation reservation) {
        if (reservation.isBeforeNow()) {
            throw new ClientException("과거 시간은 예약할 수 없습니다.");
        }
    }

    private void validateIsDuplicated(Reservation reservation) {
        if (reservationRepository.isAlreadyBooked(reservation)) {
            throw new ClientException("이미 예약이 존재합니다.");
        }
    }

    public void deleteReservation(Long id) {
        reservationRepository.delete(id);
    }

    public List<ReservationResponse> findReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
