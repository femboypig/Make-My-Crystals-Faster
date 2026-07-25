/*
 * Make My Crystals Faster
 * Copyright (C) 2026 femboyPig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.femboypig.mmcf.client;

import net.fabricmc.api.ClientModInitializer;

public final class MakeMyCrystalsFasterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // The optimization itself is applied by MultiPlayerGameModeMixin.
    }
}
