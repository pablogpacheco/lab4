package it.polito.mas.lab3.models

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import it.polito.mas.lab3.R
import it.polito.mas.lab3.data.ReservationViewModel

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class SlotAdapter(private val lifeCycleOwner: LifecycleOwner,
                  private val slots: List<Slot>,
                  private val vm: ReservationViewModel) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var selectedItem = -1

    interface OnItemClickListener {
        fun onItemClick(slot: Slot)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]

        holder.itemView.findViewById<TextView>(R.id.slotId).text = slot.id.toString()
        holder.itemView.findViewById<TextView>(R.id.slotTimeStart).text = slot.timeStart
        holder.itemView.findViewById<TextView>(R.id.slotTimeEnd).text = slot.timeEnd

        //Use an observer to see if the values have changed:
        vm.filteredData.observe(lifeCycleOwner){
            if (it.isNotEmpty()) {
                for (element in it) {
                    if (element.slot === slot.id) {
                        // Disable selection of the item
                        holder.itemView.isEnabled = false
                        holder.itemView.isClickable = false
                        holder.itemView.alpha = 0.5f
                    }
                }
            }
        }

        if (selectedItem == holder.adapterPosition) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.selected_color
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(slot)
            selectedItem = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = slots.size


}
