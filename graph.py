import csv
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.widgets import Button

def calculate_average_times(input_file):
    results = {}

    # Read results.csv and calculate averages
    with open(input_file, 'r', newline='') as file:
        reader = csv.reader(file)
        header = next(reader)  # Skip header
        for row in reader:
            target_word = row[0]
            file_name = row[1]
            execution_time = int(row[2])
            instance_count = int(row[3])
            mode = row[4]

            key = f"{target_word}_{mode}_{file_name}"
            if mode == 'Parallel':
                core_size = int(row[5])  # Assuming core size is in the 6th column
                key += f"_{core_size}"
            
            if key not in results:
                results[key] = {
                    'total_time': 0,
                    'total_count': 0,
                    'num_records': 0,
                    'book': file_name  # Store the book name in results
                }
            
            results[key]['total_time'] += execution_time
            results[key]['total_count'] += instance_count
            results[key]['num_records'] += 1

    # Calculate averages
    averages = []
    for key, value in results.items():
        avg_time = value['total_time'] / value['num_records']
        avg_count = value['total_count'] / value['num_records']
        averages.append({
            'Key': key,
            'Avg Time': avg_time,
            'Avg Count': avg_count,
            'Book': value['book']  # Add the 'Book' column to averages
        })

    return averages

def write_to_csv(output_file, averages):
    # Write averages to avgCount.csv
    with open(output_file, 'w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=['Key', 'Avg Time', 'Avg Count', 'Book'])
        writer.writeheader()
        for avg in averages:
            avg['Avg Time'] = round(avg['Avg Time'], 2)  # Round Avg Time to 2 decimal places
            writer.writerow(avg)

def create_chart(input_file):
    # Read avgCount.csv into a Pandas DataFrame
    df = pd.read_csv(input_file)

    # Extract components from 'Key' column
    df[['Word', 'Mode', 'File', 'Core']] = df['Key'].str.split('_', expand=True)
    df['Core'] = df['Core'].astype(float)  # Convert core size to float for correct sorting

    # Sort DataFrame by Core size for correct line plotting order
    df = df.sort_values(by='Core')

    # Prepare to plot
    fig, ax = plt.subplots(figsize=(10, 6))

    # Function to update plot based on selected book
    def update_plot(book):
        ax.clear()
        ax.set_xlabel('Key')
        ax.set_ylabel('Average Time')
        ax.set_title(f'Average Time by Key for {book}')

        for file_name, group in df[df['Book'] == book].groupby('File'):
            ax.plot(group['Key'], group['Avg Time'], marker='o', linestyle='-', label=file_name)

        ax.legend()
        plt.xticks(rotation=45, ha='right')
        plt.tight_layout()
        plt.draw()

    # Get unique book names
    books = df['Book'].unique().tolist()

    # Positioning for buttons
    button_axes = plt.axes([0.1, 0.95, 0.1, 0.05])
    buttons = Button(button_axes, 'Toggle Book')

    # Toggle through books
    def toggle_books(event):
        current_book = buttons.label.get_text()
        next_book = books[(books.index(current_book) + 1) % len(books)]
        buttons.label.set_text(next_book)
        update_plot(next_book)

    buttons.on_clicked(toggle_books)

    # Initialize with the first book
    update_plot(books[0])

    plt.show()

if __name__ == "__main__":
    input_file = 'avgCount.csv'
    output_file = 'avgCount.csv'

    averages = calculate_average_times(input_file)
    write_to_csv(output_file, averages)

    print(f"Average calculations completed. Results written to {output_file}.")
    create_chart(output_file)
