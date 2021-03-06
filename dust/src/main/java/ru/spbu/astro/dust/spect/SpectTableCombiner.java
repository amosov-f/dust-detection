package ru.spbu.astro.dust.spect;

import org.jetbrains.annotations.NotNull;
import ru.spbu.astro.commons.spect.SpectTable;

/**
 * User: amosov-f
 * Date: 26.10.14
 * Time: 14:23
 */
public interface SpectTableCombiner {
    @NotNull
    SpectTable combine(@NotNull SpectTable... spectTables);
}
