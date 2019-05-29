<template>
  <div class="shift">
    <h3>Shifts for {{new Date().toLocaleString('en-us', { month: 'long' })}} </h3>
    <table id="firstTable">
      <thead>
      <tr>
        <th>Name</th>
      <th v-for="row in getDaysInMonth(new Date().getMonth(), new Date().getFullYear())">
        {{row.getDate()}}
      </th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="row in shifts">
        <td>{{row.worker.lastName}} {{row.worker.firstName}}</td>
        <td v-for="day in getDaysInMonth(new Date().getMonth(), new Date().getFullYear())">
         <div v-if="compareDate(day, row.shiftDate)">
           <div class="cell">&nbsp;</div> </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import axios from 'axios';
  import Worker from 'Worker.vue';

  export interface Shift {
    id: number;
    worker: Worker;
    shiftDate: Date[];
  }
  @Component({
    components: {
    }
  })

  export default class Shifts extends Vue {
    public shifts: Shift[] = [];

    private async created() {
      const response = await axios.get('/api/shifts');
      this.shifts = await response.data;
    }

    private compareDate(day : Date, days: Date[]) {
      var dayIncome;
      for (dayIncome of days) {
       if (day.getDate() == new Date(dayIncome).getDate()) {
         return true;
       }
      }
      return false
    }

    private getDaysInMonth(month : number, year : number) {
      // Since no month has fewer than 28 days
      var date = new Date(year, month, 1);
      var days = [];
      while (date.getMonth() === month) {
        days.push(new Date(date));
        date.setDate(date.getDate() + 1);
      }
      return days;
    }
  }

</script>
<style>
  table {
    font-family: 'Open Sans', sans-serif;
    width: 80%;
    border-collapse: collapse;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    margin: 0 auto;

  }

  table th {
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 5px;
    min-width: 20px;
    text-align: left;

  }
  tr:hover {background-color: #f5f5f5;}

  table td {
    padding: 1px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    border-right: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    text-align: left;
  }
  td:hover {
    background-color: #42b983;
  }

  div.cell{
    background-color: #206600;
    padding-left: 0px;
    padding-right: 0px;
    padding-top: 0px;
    padding-bottom: 0px;
  }

</style>
