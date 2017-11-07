package xx.projmap.scene2

import xx.projmap.geometry.GeoPoint
import xx.projmap.geometry.MutPoint

class Origin(origin: GeoPoint = MutPoint()) : Component() {

    val origin: MutPoint = origin.toMutable()

}