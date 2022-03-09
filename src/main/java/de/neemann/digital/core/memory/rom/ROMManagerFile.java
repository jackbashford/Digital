/*
 * Copyright (c) 2022 Helmut Neemann.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.core.memory.rom;

import de.neemann.digital.core.memory.DataField;
import de.neemann.digital.core.memory.importer.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * New rom manager which is able to deal with data stored in a file
 */
public class ROMManagerFile extends ROMMangerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ROMManagerFile.class);

    private final HashMap<String, RomContainer> roms;

    /**
     * Constructor to convert the old rom manager.
     *
     * @param rm the old rom manager
     */
    public ROMManagerFile(ROMManager rm) {
        this();
        LOGGER.info("convert old rom manager");
        for (Map.Entry<String, DataField> df : rm.getRoms().entrySet())
            roms.put(df.getKey(), new RomContainerDataField(df.getValue()));
    }

    /**
     * Creates a new instance.
     */
    public ROMManagerFile() {
        roms = new HashMap<>();
    }

    @Override
    public boolean isEmpty() {
        return roms.isEmpty();
    }

    @Override
    public DataField getRom(String label, int dataBits) throws IOException {
        if (roms == null)
            return null;
        final RomContainer rc = roms.get(label);
        if (rc == null)
            return null;
        return rc.getDataField(dataBits);
    }

    /**
     * Returns the rom container
     *
     * @param label the label
     * @return the rom container
     */
    public RomContainer getRomContainer(String label) {
        return roms.get(label);
    }

    /**
     * Adds a container to this manager.
     * Is called by the editor.
     *
     * @param label        the label
     * @param romContainer the data container
     */
    public void addContainer(String label, RomContainer romContainer) {
        roms.put(label, romContainer);
    }

    /**
     * Interface to access the rom data
     */
    public interface RomContainer {
        /**
         * returns the data filed to init the rom
         *
         * @param dataBits the data bit used
         * @return the data field
         * @throws IOException IOException
         */
        DataField getDataField(int dataBits) throws IOException;
    }

    /**
     * The data based container
     */
    public static final class RomContainerDataField implements RomContainer {
        private final DataField dataField;

        /**
         * Creates a new data based container
         *
         * @param dataField the data to store
         */
        public RomContainerDataField(DataField dataField) {
            dataField.trim();
            this.dataField = new DataField(dataField);
        }

        @Override
        public DataField getDataField(int dataBits) {
            return dataField;
        }
    }

    /**
     * The file based container
     */
    public static final class RomContainerFile implements RomContainer {
        private final File romData;
        private final boolean bigEndian;

        /**
         * Creates a new file based container
         *
         * @param romData   the file
         * @param bigEndian the big endian load mode
         */
        public RomContainerFile(File romData, boolean bigEndian) {
            this.romData = romData;
            this.bigEndian = bigEndian;
        }

        @Override
        public DataField getDataField(int dataBits) throws IOException {
            return Importer.read(romData, dataBits, bigEndian);
        }

        /**
         * @return the file which holds the data
         */
        public File getFile() {
            return romData;
        }

        /**
         * @return big endian load mode
         */
        public boolean isBigEndian() {
            return bigEndian;
        }
    }
}