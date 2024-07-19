package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class PokedexApp {
    private static final String API_URL = "https://pokeapi.co/api/v2/pokemon?limit=30&offset=0";

    public PokedexApp() throws IOException {
        JFrame jFrame = new JFrame("Pokedex");
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Nama");

        List<Pokemon> pokemonList = fetchPokemonData();
        for (Pokemon pokemon : pokemonList) {
            tableModel.addRow(new Object[] { pokemon.getId(), pokemon.getName() });
        }

        JTable table = new JTable(tableModel);

        JScrollPane pane = new JScrollPane(table);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    int pokemonId = (int) table.getValueAt(row, 0);
                    String pokemonName = table.getValueAt(row, 1).toString();
                    
                    String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonId + ".png";
                    displayImageInNewFrame(pokemonName, imageUrl);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.add(pane);

        jFrame.getContentPane().add(BorderLayout.CENTER, panel);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.setBounds(300, 200, 300, 400);
    }

    private List<Pokemon> fetchPokemonData() throws IOException {
        List<Pokemon> pokemonList = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.body().string());
                JsonNode resultsNode = rootNode.get("results");

                if (resultsNode.isArray()) {
                    int id = 1;
                    for (JsonNode pokemonNode : resultsNode) {
                        String name = pokemonNode.get("name").asText();
                        pokemonList.add(new Pokemon(id++, name));
                    }
                }
            }
        }

        return pokemonList;
    }

        private void displayImageInNewFrame(String pokemonName, String imageUrl) {
        JFrame imageFrame = new JFrame(pokemonName);
        imageFrame.setSize(300, 400);
        imageFrame.setLayout(new BorderLayout());

        try {
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageFrame.add(imageLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        imageFrame.add(panel, BorderLayout.NORTH);
        imageFrame.setVisible(true);
    }
}

class Pokemon {
    private final int id;
    private final String name;

    public Pokemon(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}