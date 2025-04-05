import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BusReservationSystem extends JFrame {
    private BusManager busManager;
    private CustomerManager customerManager;
    private ReservationManager reservationManager;

    private JComboBox<String> busComboBox;
    private JTextField customerIdField;
    private JTextField customerNameField;
    private JTextField seatsField;
    private JButton addCustomerButton;
    private JButton reserveButton;
    private JTextArea reservationTextArea;

    public BusReservationSystem() {
        busManager = new BusManager();
        customerManager = new CustomerManager();
        reservationManager = new ReservationManager(busManager, customerManager);

        busManager.addBus(new Bus(1, "Express", 40));
        busManager.addBus(new Bus(2, "Local", 50));

        setTitle("Bus Reservation System");
        setSize(800, 600); // Make the interface larger
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window

        // Adding the title label at the top
        JLabel titleLabel = new JLabel("BUS RESERVATION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(new JLabel("Select Bus:"), gbc);

        gbc.gridx = 1;
        busComboBox = new JComboBox<>();
        for (Bus bus : busManager.getAllBuses()) {
            busComboBox.addItem(bus.getBusType() + " (ID: " + bus.getBusId() + ")");
        }
        inputPanel.add(busComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Customer ID:"), gbc);

        gbc.gridx = 1;
        customerIdField = new JTextField(10);
        inputPanel.add(customerIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        customerNameField = new JTextField(10);
        inputPanel.add(customerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Number of Seats:"), gbc);

        gbc.gridx = 1;
        seatsField = new JTextField(10);
        inputPanel.add(seatsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addCustomerButton = new JButton("Add Customer");
        inputPanel.add(addCustomerButton, gbc);

        gbc.gridx = 1;
        reserveButton = new JButton("Reserve Seat");
        reserveButton.setEnabled(false); // Disable reserve button initially
        inputPanel.add(reserveButton, gbc);

        add(inputPanel, BorderLayout.CENTER);

        reservationTextArea = new JTextArea();
        reservationTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Make text area font bigger
        add(new JScrollPane(reservationTextArea), BorderLayout.SOUTH);

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeReservation();
            }
        });

        updateReservations();
    }

    private void addCustomer() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            String customerName = customerNameField.getText().trim();

            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            customerManager.addCustomer(new Customer(customerId, customerName));
            JOptionPane.showMessageDialog(this, "Customer added successfully.");
            reserveButton.setEnabled(true); // Enable reserve button after adding customer
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Customer ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void makeReservation() {
        int busId = busComboBox.getSelectedIndex() + 1;
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            int seats = Integer.parseInt(seatsField.getText().trim());
            boolean success = reservationManager.makeReservation(customerId, busId, seats);
            if (success) {
                JOptionPane.showMessageDialog(this, "Seats reserved successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reserve seats. Not enough available seats.");
            }
            updateReservations();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReservations() {
        reservationTextArea.setText("");
        List<Reservation> reservations = reservationManager.getAllReservations();
        for (Reservation reservation : reservations) {
            Customer customer = customerManager.getCustomerById(reservation.getCustomerId());
            Bus bus = busManager.getBusById(reservation.getBusId());
            reservationTextArea.append("Reservation ID: " + reservation.getReservationId() +
                    ", Customer: " + customer.getName() +
                    ", Bus: " + bus.getBusType() +
                    ", Seats: " + reservation.getSeats() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BusReservationSystem().setVisible(true);
            }
        });
    }
}

class Bus {
    private int busId;
    private String busType;
    private int totalSeats;
    private int availableSeats;

    public Bus(int busId, String busType, int totalSeats) {
        this.busId = busId;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public int getBusId() {
        return busId;
    }

    public String getBusType() {
        return busType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean reserveSeats(int numberOfSeats) {
        if (availableSeats >= numberOfSeats) {
            availableSeats -= numberOfSeats;
            return true;
        }
        return false;
    }
}

class Customer {
    private int customerId;
    private String name;

    public Customer(int customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

class Reservation {
    private int reservationId;
    private int busId;
    private int customerId;
    private int seats;

    public Reservation(int reservationId, int busId, int customerId, int seats) {
        this.reservationId = reservationId;
        this.busId = busId;
        this.customerId = customerId;
        this.seats = seats;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getBusId() {
        return busId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSeats() {
        return seats;
    }
}

class BusManager {
    private List<Bus> buses;

    public BusManager() {
        this.buses = new ArrayList<>();
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public Bus getBusById(int busId) {
        for (Bus bus : buses) {
            if (bus.getBusId() == busId) {
                return bus;
            }
        }
        return null;
    }

    public List<Bus> getAllBuses() {
        return buses;
    }
}

class CustomerManager {
    private List<Customer> customers;

    public CustomerManager() {
        this.customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public Customer getCustomerById(int customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return customer;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }
}

class ReservationManager {
    private List<Reservation> reservations;
    private BusManager busManager;
    private CustomerManager customerManager;
    private int reservationCounter;

    public ReservationManager(BusManager busManager, CustomerManager customerManager) {
        this.reservations = new ArrayList<>();
        this.busManager = busManager;
        this.customerManager = customerManager;
        this.reservationCounter = 1;
    }

    public boolean makeReservation(int customerId, int busId, int seats) {
        Bus bus = busManager.getBusById(busId);
        Customer customer = customerManager.getCustomerById(customerId);

        if (bus != null && customer != null && bus.reserveSeats(seats)) {
            Reservation reservation = new Reservation(reservationCounter++, busId, customerId, seats);
            reservations.add(reservation);
            return true;
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }
}
