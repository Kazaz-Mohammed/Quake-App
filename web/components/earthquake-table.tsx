"use client"

import { useState } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"
import type { Earthquake } from "@/lib/sample-data"

interface EarthquakeTableProps {
  data: Earthquake[]
}

export default function EarthquakeTable({ data }: EarthquakeTableProps) {
  const [searchTerm, setSearchTerm] = useState("")
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 5

  // Filter data based on search term
  const filteredData = data.filter(
    (item) =>
      item.location.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.id.toString().includes(searchTerm) ||
      item.magnitude.toString().includes(searchTerm),
  )

  // Paginate data
  const indexOfLastItem = currentPage * itemsPerPage
  const indexOfFirstItem = indexOfLastItem - itemsPerPage
  const currentItems = filteredData.slice(indexOfFirstItem, indexOfLastItem)
  const totalPages = Math.ceil(filteredData.length / itemsPerPage)

  // Format date
  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleString()
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center space-x-2">
        <div className="relative flex-1">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-500" />
          <Input
            type="text"
            placeholder="Search earthquakes..."
            className="pl-8"
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value)
              setCurrentPage(1)
            }}
          />
        </div>
      </div>

      <div className="rounded-md border bg-white">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Magnitude</TableHead>
              <TableHead className="hidden md:table-cell">Latitude</TableHead>
              <TableHead className="hidden md:table-cell">Longitude</TableHead>
              <TableHead>Location</TableHead>
              <TableHead className="hidden md:table-cell">Timestamp</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {currentItems.length > 0 ? (
              currentItems.map((earthquake) => (
                <TableRow key={earthquake.id}>
                  <TableCell className="font-medium">{earthquake.id}</TableCell>
                  <TableCell>
                    <span
                      className={`inline-block rounded-full px-2 py-1 text-xs font-semibold ${
                        earthquake.magnitude >= 6
                          ? "bg-red-100 text-red-800"
                          : earthquake.magnitude >= 4
                            ? "bg-yellow-100 text-yellow-800"
                            : "bg-green-100 text-green-800"
                      }`}
                    >
                      {earthquake.magnitude.toFixed(1)}
                    </span>
                  </TableCell>
                  <TableCell className="hidden md:table-cell">{earthquake.latitude.toFixed(4)}</TableCell>
                  <TableCell className="hidden md:table-cell">{earthquake.longitude.toFixed(4)}</TableCell>
                  <TableCell>{earthquake.location}</TableCell>
                  <TableCell className="hidden md:table-cell">{formatDate(earthquake.timestamp)}</TableCell>
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={6} className="h-24 text-center">
                  No results found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
                className={currentPage === 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
              />
            </PaginationItem>

            {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
              <PaginationItem key={page}>
                <PaginationLink onClick={() => setCurrentPage(page)} isActive={currentPage === page}>
                  {page}
                </PaginationLink>
              </PaginationItem>
            ))}

            <PaginationItem>
              <PaginationNext
                onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
                className={currentPage === totalPages ? "pointer-events-none opacity-50" : "cursor-pointer"}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  )
}
