package main;

import infra.exception.GuestNotFoundException;
import main.domain.GerenciadorDeSalas;
import main.domain.MarcadorDeReuniao;
import main.domain.Reserva;
import main.infra.exception.BookingNotFoundException;
import main.infra.exception.RoomAlreadyReservedException;
import main.infra.exception.RoomNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final MarcadorDeReuniao marcadorDeReuniao = new MarcadorDeReuniao();
    private static final GerenciadorDeSalas gerenciadorDeSalas = new GerenciadorDeSalas();

    private static final List<Reserva> reservas = new LinkedList<>();

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        while (!input.equals("fim")) {

            switch (input) {
                case "marcar":
                    handleScheduling(scanner);
                    break;
                case "indicar":
                    handleDesignatingAvailability(scanner);
                    break;
                case "sobreposicao":
                    handlePrintingOverlap();
                    break;
                case "adiciona":
                    handleAddingRoom(scanner);
                    break;
                case "remove":
                    handleRemovingRoom(scanner);
                    break;
                case "reserva":
                    handleBookingRoom(scanner);
                    break;
                case "cancela":
                    handleCancelingRoomBooking(scanner);
                    break;
                case "reservas":
                    handlePrintingBookings(scanner);
                    break;
                default:
                    System.err.println("Insira um comando v??lido");
            }

            input = scanner.nextLine();
        }
    }

    private static void handleScheduling(final Scanner scanner) {
        System.out.println("Entre quais dias voc?? gostaria de marcar a reuni??o?");

        System.out.println("Insira o dia inicial (formato yyyy-MM-dd)");
        final String startStr = scanner.nextLine();

        System.out.println("Insira o dia final (formato yyyy-MM-dd)");
        final String endStr = scanner.nextLine();

        System.out.println("Insira o email dos convidados separado por ';' (ex: convidado1@email.com;convidado2@email.com");
        final String guestsStr = scanner.nextLine();

        try {
            marcadorDeReuniao.marcarReuniaoEntre(LocalDate.parse(startStr),
                    LocalDate.parse(endStr), Arrays.asList(guestsStr.split(";")));
        } catch (DateTimeParseException exception) {
            System.err.println("Insira as datas no formato indicado");
        }
    }

    private static void handleDesignatingAvailability(final Scanner scanner) {
        System.out.println("Indique em quais hor??rios voc?? est?? dispon??vel");

        System.out.println("Insira o seu email");
        final String emailStr = scanner.nextLine();

        System.out.println("Insira o horario inicial (formato yyyy-MM-dd HH:mm)");
        final String startStr = scanner.nextLine();

        System.out.println("Insira o horario final (formato yyyy-MM-dd HH:mm)");
        final String endStr = scanner.nextLine();

        try {
            marcadorDeReuniao.indicaDisponibilidade(emailStr,
                    LocalDateTime.parse(startStr, formatter), LocalDateTime.parse(endStr, formatter));
        } catch (DateTimeParseException exception) {
            System.err.println("Insira as datas no formato indicado");
        } catch (GuestNotFoundException exception) {
            System.err.println("Participante n??o encontrado");
        }
    }

    private static void handlePrintingOverlap() {
        marcadorDeReuniao.mostraSobreposicao();
    }

    private static void handleAddingRoom(final Scanner scanner) {
        System.out.println("Insira o nome da sala");
        final String name = scanner.nextLine();

        System.out.println("Insira a capacidade m??xima da sala");
        final String maxCapacity = scanner.nextLine();

        System.out.println("Insira uma descri????o da sala");
        final String description = scanner.nextLine();

        try {
            gerenciadorDeSalas.adicionaSala(name, Integer.parseInt(maxCapacity), description);
        } catch (NumberFormatException exception) {
            System.err.println("Insira um n??mero inteiro v??lido");
        }
    }

    private static void handleRemovingRoom(final Scanner scanner) {
        System.out.println("Insira o nome da sala que ser?? removida");
        final String nome = scanner.nextLine();

        try {
            gerenciadorDeSalas.removeSalaChamada(nome);
        } catch (RoomNotFoundException exception) {
            System.err.println("Sala n??o encontrada");
        }
    }

    private static void handleBookingRoom(final Scanner scanner) {
        System.out.println("Insira o nome da sala que ser?? reservada");
        final String name = scanner.nextLine();

        System.out.println("Insira o hor??rio inicial (formato yyyy-MM-dd HH:mm)");
        final String start = scanner.nextLine();

        System.out.println("Insira o hor??rio final (formato yyyy-MM-dd HH:mm)");
        final String end = scanner.nextLine();

        try {
            final Reserva reserva = gerenciadorDeSalas.reservaSalaChamada(name,
                    LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter));

            reservas.add(reserva);
        } catch (RoomAlreadyReservedException exception) {
            System.err.println("Esta sala j?? est?? reservada neste hor??rio");
        } catch (RoomNotFoundException exception) {
            System.err.println("Sala n??o encontrada");
        } catch (DateTimeParseException exception) {
            System.err.println("Insira as datas no formato indicado");
        }
    }

    private static void handleCancelingRoomBooking(final Scanner scanner) {
        System.out.println("Insira o nome da sala");
        final String nome = scanner.nextLine();
        System.out.println("Insira o hor??rio de in??cio da reserva da sala (formato yyyy-MM-dd HH:mm)");
        final String inicio = scanner.nextLine();
        System.out.println("Insira o hor??rio de fim da reserva da sala (formato yyyy-MM-dd HH:mm)");
        final String fim = scanner.nextLine();

        try {
            final Reserva reserva = findReservaByNameAndInterval(nome,
                    LocalDateTime.parse(inicio, formatter), LocalDateTime.parse(fim, formatter));
            gerenciadorDeSalas.cancelaReserva(reserva);
        } catch (RoomNotFoundException exception) {
            System.err.println("Sala n??o foi encontrada");
        } catch (DateTimeParseException exception) {
            System.err.println("Insira as datas no formato indicado");
        } catch (BookingNotFoundException exception) {
            System.err.println("Reserva n??o foi encontrada");
        }
    }

    private static void handlePrintingBookings(final Scanner scanner) {
        System.out.println("Insira o nome da sala");
        final String name = scanner.nextLine();

        try {
            gerenciadorDeSalas.imprimeReservasPraSala(name);
        } catch (RoomNotFoundException exception) {
            System.err.println("Sala n??o encontrada");
        }
    }

    private static Reserva findReservaByNameAndInterval(String name, LocalDateTime start, LocalDateTime end)
            throws BookingNotFoundException {
        for (Reserva reserva : reservas) {
            if (reserva.inicio().isEqual(start) &&
                    reserva.fim().isEqual(end) && reserva.sala().getNome().equals(name)) {
                return reserva;
            }
        }

        throw new BookingNotFoundException();
    }
}
